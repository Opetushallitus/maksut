import AxeBuilder from '@axe-core/playwright';
import { hmac } from '@noble/hashes/hmac';
import { sha512 } from '@noble/hashes/sha512';
import {
  test,
  Page,
  expect,
  chromium,
  APIRequestContext,
} from '@playwright/test';
import { v4 as uuid } from 'uuid';

test.describe.configure({ mode: 'serial' });

const MERCHANT_KEY = 'sikrot';
const ACCOUNT_ID = '12345';

const BACKEND_URL = 'http://localhost:19033';
const APP_URL = 'http://localhost:19033';

let userPage: Page;
let apiContext: APIRequestContext;

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const getSignature: (obj: any) => string = (obj) => {
  const strToSign = Object.keys(obj).reduce((str, key) => {
    return str + key + ':' + obj[key] + '\n';
  }, '');
  return Buffer.from(hmac(sha512, MERCHANT_KEY, strToSign)).toString('hex');
};

interface Invoice {
  key: string;
  secret: string;
  payerEmail: string;
}

const createInvoice: (
  apiContext: APIRequestContext,
) => Promise<Invoice> = async () => {
  const dueDate = new Date(new Date().getTime() + 7 * 24 * 60 * 60 * 1000);
  const dueDateIso = dueDate.toISOString().split('T')[0];
  const invoiceKey = 'KKHA' + uuid();
  const payerEmail = `${uuid()}@maksut-local.test`;
  const newInvoice = await apiContext.post(`${BACKEND_URL}/maksut/api/lasku`, {
    data: {
      'due-date': dueDateIso,
      'first-name': 'test1',
      'last-name': 'test1',
      'order-id': invoiceKey,
      origin: 'kkhakemusmaksu',
      vat: '22',
      email: payerEmail,
      'due-days': 21,
      metadata: {
        alkamiskausi: 'kausi_k',
        alkamisvuosi: dueDate.getFullYear() + 1,
      },
      reference: invoiceKey,
      amount: '256',
      'extend-deadline': true,
    },
  });
  expect(newInvoice.ok()).toBeTruthy();
  const newInvoiceResponseJson = await newInvoice.json();

  return {
    key: invoiceKey,
    secret: newInvoiceResponseJson.secret,
    payerEmail,
  };
};

const goTo = async (page: Page, route: string) => {
  await page.goto(`${APP_URL}${route}`);
};

const expectPageAccessibilityOk = async (page: Page) => {
  const accessibilityScanResults = await new AxeBuilder({ page }).analyze();
  expect(accessibilityScanResults.violations).toEqual([]);
};

const assertInvoiceMarkedPaid = async (secret: string) => {
  await expect(userPage).toHaveURL(`${APP_URL}/maksut/fi/?secret=${secret}`, {
    timeout: 20000,
  });
  await expect(userPage.getByText('Maksettu', { exact: true })).toBeVisible();
};

const assertEmailsSent = async (payerEmail: string) => {
  await expect
    .poll(async () => {
      const emailResponse = await apiContext.get(
        `http://localhost:1080/messages`,
      );
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      const emails: any[] = await emailResponse.json();
      return emails.filter((email) =>
        email.recipients.includes(`<${payerEmail}>`),
      );
    })
    .toHaveLength(1);
};

const assertProviderTermsVisible = async () => {
  await expect(userPage.getByText('Maksupalvelutarjoaja')).toBeVisible();
  await expect(
    userPage.getByText(
      'Maksunvälityspalvelun toteuttajana ja maksupalveluntarjoajana',
    ),
  ).toBeVisible();
  await expect(
    userPage.getByText('Paytrail Oyj, y-tunnus: 2122839-7'),
  ).toBeVisible();
};

const assertProviderTermsNotVisible = async (secret: string) => {
  await expect(userPage).toHaveURL(`${APP_URL}/maksut/fi/?secret=${secret}`, {
    timeout: 20000,
  });

  await expect(userPage.getByText('Maksupalvelutarjoaja')).not.toBeVisible();
  await expect(
    userPage.getByText(
      'Maksunvälityspalvelun toteuttajana ja maksupalveluntarjoajana',
    ),
  ).not.toBeVisible();
  await expect(
    userPage.getByText('Paytrail Oyj, y-tunnus: 2122839-7'),
  ).not.toBeVisible();
};

const openFullTerms = async () => {
  // Avataan käyttöehdot ja odotetaan, että ne aukeavat
  await userPage.getByRole('button', { name: 'hakemusmaksun ehdot' }).click();
  await userPage.getByText('Hakemusmaksun suuruus').waitFor();
  await userPage.getByRole('button', { name: 'Hyväksyn ehdot' }).isEnabled();
  await userPage.waitForTimeout(100);
};

const closeFullTerms = async () => {
  await userPage.getByRole('button', { name: 'Sulje' }).click();
};

const assertTermsVisible = async () => {
  await openFullTerms();

  await expect(
    userPage.getByText(
      'Hyväksymällä nämä ehdot vahvistan ymmärtäväni ja hyväksyväni seuraavat hakemusmaksua koskevat maksu- ja toimitusehdot.',
      { exact: true },
    ),
  ).toBeVisible();
  await expect(
    userPage
      .getByRole('listitem')
      .filter({ hasText: 'Hakija vastaa kaikkien' }),
  ).toHaveAttribute('value', '19');

  await closeFullTerms();

  await expect(
    userPage.getByText(
      'Hyväksymällä nämä ehdot vahvistan ymmärtäväni ja hyväksyväni seuraavat hakemusmaksua koskevat maksu- ja toimitusehdot.',
      { exact: true },
    ),
  ).not.toBeVisible();
};

const assertCloseAndAcceptTermsWorks = async () => {
  await expect(
    userPage.getByRole('checkbox', { name: 'Olen lukenut ja hyväksyn' }),
  ).not.toBeChecked();
  await expect(
    userPage.getByRole('link', { name: 'Siirry maksamaan' }),
  ).toBeDisabled();

  await openFullTerms();
  await userPage.getByRole('button', { name: 'Hyväksyn ehdot' }).click();

  await expect(
    userPage.getByRole('checkbox', { name: 'Olen lukenut ja hyväksyn' }),
  ).toBeChecked();
  await expect(
    userPage.getByRole('link', { name: 'Siirry maksamaan' }),
  ).toBeEnabled();
};

test.beforeAll(async ({ playwright }) => {
  const browser = await chromium.launch({
    headless: true,
    args: ['--disable-web-security'],
  });
  const context = await browser.newContext({ ignoreHTTPSErrors: true });
  userPage = await context.newPage();
  apiContext = await playwright.request.newContext({ ignoreHTTPSErrors: true });

  // kirjaudutaan ataruna sisään maksut-sovellukseen
  await apiContext.get(`${BACKEND_URL}/maksut/auth/cas?ticket=abc`);
});

test.afterAll(async () => {
  await userPage.close();
  await apiContext.dispose();
});

test('Accessibility', async () => {
  // luodaan ataruna uusi lasku
  const invoice = await createInvoice(apiContext);

  // mennään käyttäjänä maksusivulle
  await goTo(userPage, `/maksut/fi?secret=${invoice.secret}`);

  // saavutettavuuden pitäisi olla ok
  await expectPageAccessibilityOk(userPage);

  await openFullTerms();

  // saavutettavuuden pitäisi olla täälläkin ok
  await expectPageAccessibilityOk(userPage);
});

test.describe('Mocked Paytrail', () => {
  test('Paytrail mocked maksuflow', async () => {
    // luodaan ataruna uusi lasku
    const invoice = await createInvoice(apiContext);

    const checkoutData = {
      'checkout-account': ACCOUNT_ID,
      'checkout-algorithm': 'sha512',
      'checkout-amount': 25600,
      'checkout-provider': 'osuuspankki',
      'checkout-reference': invoice.key,
      'checkout-stamp': '65905948-5161-4569-9194-eaf131a6f0e7',
      'checkout-status': 'ok',
      'checkout-transaction-id': 'ca73be38-d703-11ee-b7f6-37338e14f841',
    };

    /*
        Stubataan Paytrailin vastaus uuden maksutapahtuman luontiin. Normaalitilanteessa maksut-palvelu kutsuu
        Paytrailia joka palauttaa linkin johon maksaja ohjataan. Maksutapahtuman jälkeen Paytrail ohjaa maksajan
        takaisin maksut-sovellukseen, joka rekisteröi maksun. Mokatussa testissä Paytrail-mockki palauttaa suoraan
        linkin takaisin maksut-sovellukseen.
        */
    const callbackUrl =
      `/maksut/api/payment/paytrail/success?locale=fi` +
      `&secret=${invoice.secret}` +
      `&checkout-account=${checkoutData['checkout-account']}` +
      `&checkout-algorithm=${checkoutData['checkout-algorithm']}` +
      `&checkout-amount=${checkoutData['checkout-amount']}` +
      `&checkout-stamp=${checkoutData['checkout-stamp']}` +
      `&checkout-reference=${checkoutData['checkout-reference']}` +
      `&checkout-status=${checkoutData['checkout-status']}` +
      `&checkout-provider=${checkoutData['checkout-provider']}` +
      `&checkout-transaction-id=${checkoutData['checkout-transaction-id']}` +
      `&signature=${getSignature(checkoutData)}`;

    const newStub = await apiContext.post(
      `http://localhost:9040/__admin/mappings`,
      {
        data: {
          request: { url: '/payments', method: 'POST' },
          response: { status: 200, body: `{"href": "${callbackUrl}"}` },
        },
      },
    );
    expect(newStub.ok()).toBeTruthy();

    // mennään käyttäjänä maksusivulle
    await goTo(userPage, `/maksut/fi?secret=${invoice.secret}`);

    await expect(
      userPage.getByRole('link', { name: 'Siirry maksamaan' }),
    ).toBeDisabled();

    await assertProviderTermsVisible();
    await assertTermsVisible();
    await assertCloseAndAcceptTermsWorks();

    // Hyväksytään ehdot
    await expect(
      userPage.getByText('Olen lukenut ja hyväksyn hakemusmaksun ehdot'),
    ).toBeVisible();
    await userPage.getByRole('checkbox').check();

    // käynnistetään käyttäjänä maksuflow
    await userPage.getByRole('link', { name: 'Siirry maksamaan' }).click();

    // tämä osuus mokattu, selain palaa automaattisesti takaisin maksut-sovellukseen

    // varmistetaan että ollaan käyttäjänä palattu maksuihin tehdyn maksun sivulle ja maksu merkitty maksetuksi
    await assertInvoiceMarkedPaid(invoice.secret);

    // Varmistetaan, että maksupalveluntarjoajan käyttöehdot eivät näy, kun maksu on jo maksettu
    await assertProviderTermsNotVisible(invoice.secret);

    // varmistetaan että kuitit on lähetetty
    await assertEmailsSent(invoice.payerEmail);
  });
});
