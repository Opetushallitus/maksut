import AxeBuilder from "@axe-core/playwright";
import { hmac } from "@noble/hashes/hmac";
import { sha512 } from "@noble/hashes/sha512";
import {
  test,
  Page,
  expect,
  chromium,
  APIRequestContext,
} from "@playwright/test";
import { v4 as uuid } from "uuid";

test.describe.configure({ mode: "serial" });

const MERCHANT_KEY =
  process.env.WITH_PAYTRAIL == "TRUE" ? "SAIPPUAKAUPPIAS" : "sikrot";
const ACCOUNT_ID = process.env.WITH_PAYTRAIL == "TRUE" ? "375917" : "12345";

let userPage: Page;
let apiContext: APIRequestContext;

const getSignature: (obj: any) => string = (obj) => {
  const strToSign = Object.keys(obj).reduce((str, key) => {
    return str + key + ":" + obj[key] + "\n";
  }, "");
  return Buffer.from(hmac(sha512, MERCHANT_KEY, strToSign)).toString("hex");
};

type Invoice = {
  key: string;
  secret: string;
  payerEmail: string;
};

const createInvoice: (
  apiContext: APIRequestContext,
) => Promise<Invoice> = async () => {
  const invoiceKey = "K" + uuid();
  const payerEmail = `${uuid()}@maksut-local.test`;
  const newInvoice = await apiContext.post(`/maksut/api/lasku-tutu`, {
    data: {
      "application-key": invoiceKey,
      "first-name": "test1",
      "last-name": "test1",
      email: payerEmail,
      amount: "256",
      "due-date": "2030-03-03",
      index: 1,
    },
  });
  expect(newInvoice.ok()).toBeTruthy();
  const newInvoiceResponseJson = await newInvoice.json();

  return {
    key: `TTU${invoiceKey}-1`,
    secret: newInvoiceResponseJson.secret,
    payerEmail,
  };
};

const expectPageAccessibilityOk = async (page: Page) => {
  const accessibilityScanResults = await new AxeBuilder({ page }).analyze();
  await expect(accessibilityScanResults.violations).toEqual([]);
};

const assertInvoiceMarkedPaid: (secret: string) => void = async (secret) => {
  await expect(userPage).toHaveURL(`/maksut/?secret=${secret}&locale=fi`, {
    timeout: 20000,
  });
  await expect(userPage.getByText("Maksettu", { exact: true })).toBeVisible();
};

const assertEmailsSent: (payerEmail: string) => void = async (payerEmail) => {
  await expect
    .poll(async () => {
      const emailResponse = await apiContext.get(
        `http://localhost:1080/messages`,
      );
      const emails: Array<any> = await emailResponse.json();
      return emails.filter((email) =>
        email.recipients.includes(`<${payerEmail}>`),
      );
    })
    .toHaveLength(2);
};

test.beforeAll(async ({ playwright }) => {
  const browser = await chromium.launch({
    headless: true,
    args: ["--disable-web-security"],
  });
  const context = await browser.newContext({ ignoreHTTPSErrors: true });
  userPage = await context.newPage();
  apiContext = await playwright.request.newContext({ ignoreHTTPSErrors: true });

  // kirjaudutaan ataruna sisään maksut-sovellukseen
  await apiContext.get("/maksut/auth/cas?ticket=abc");
});

test.afterAll(async () => {
  await userPage.close();
  await apiContext.dispose();
});

test("Accessibility", async () => {
  // luodaan ataruna uusi lasku
  const invoice = await createInvoice(apiContext);

  // mennään käyttäjänä maksusivulle
  await userPage.goto(`/maksut/?secret=${invoice.secret}&locale=fi`);

  // saavutettavuuden pitäisi olla ok
  await expectPageAccessibilityOk(userPage);
});

test.describe("Real Paytrail", () => {
  test.skip()
  test.skip(() => process.env.WITH_PAYTRAIL != "TRUE");

  test("Paytrail maksuflow", async () => {
    // luodaan ataruna uusi lasku
    const invoice = await createInvoice(apiContext);

    // mennään käyttäjänä maksusivulle
    await userPage.goto(`/maksut/?secret=${invoice.secret}&locale=fi`);

    // käynnistetään käyttäjänä maksuflow
    await userPage.getByRole("link", { name: "Siirry maksamaan" }).click();

    // maksetaan käyttäjänä
    await userPage.getByRole("img", { name: "OP", exact: true }).click();
    await userPage.getByRole("button", { name: "OP", exact: true }).click();

    // varmistetaan että ollaan käyttäjänä palattu maksuihin tehdyn maksun sivulle ja maksu merkitty maksetuksi
    await assertInvoiceMarkedPaid(invoice.secret);

    // varmistetaan että kuitit on lähetetty käyttäjälle sähköpostissa
    await assertEmailsSent(invoice.payerEmail);
  });
});

test.describe("Mocked Paytrail", () => {
  test.skip()
  test.skip(() => process.env.WITH_PAYTRAIL == "TRUE");

  test("Paytrail mocked maksuflow", async () => {
    // luodaan ataruna uusi lasku
    const invoice = await createInvoice(apiContext);

    const checkoutData = {
      "checkout-account": ACCOUNT_ID,
      "checkout-algorithm": "sha512",
      "checkout-amount": 25600,
      "checkout-provider": "osuuspankki",
      "checkout-reference": invoice.key,
      "checkout-stamp": "65905948-5161-4569-9194-eaf131a6f0e7",
      "checkout-status": "ok",
      "checkout-transaction-id": "ca73be38-d703-11ee-b7f6-37338e14f841",
    };

    /*
        Stubataan Paytrailin vastaus uuden maksutapahtuman luontiin. Normaalitilanteessa maksut-palvelu kutsuu
        Paytrailia joka palauttaa linkin johon maksaja ohjataan. Maksutapahtuman jälkeen Paytrail ohjaa maksajan
        takaisin maksut-sovellukseen, joka rekisteröi maksun. Mokatussa testissä Paytrail-mockki palauttaa suoraan
        linkin takaisin maksut-sovellukseen.
        */
    const callbackUrl =
      `/maksut/api/payment/paytrail/success?tutulocale=fi` +
      `&tutusecret=${invoice.secret}` +
      `&checkout-account=${checkoutData["checkout-account"]}` +
      `&checkout-algorithm=${checkoutData["checkout-algorithm"]}` +
      `&checkout-amount=${checkoutData["checkout-amount"]}` +
      `&checkout-stamp=${checkoutData["checkout-stamp"]}` +
      `&checkout-reference=${checkoutData["checkout-reference"]}` +
      `&checkout-status=${checkoutData["checkout-status"]}` +
      `&checkout-provider=${checkoutData["checkout-provider"]}` +
      `&checkout-transaction-id=${checkoutData["checkout-transaction-id"]}` +
      `&signature=${getSignature(checkoutData)}`;

    const newStub = await apiContext.post(
      `http://localhost:9040/__admin/mappings`,
      {
        data: {
          request: { url: "/payments", method: "POST" },
          response: { status: 200, body: `{"href": "${callbackUrl}"}` },
        },
      },
    );
    await expect(newStub.ok()).toBeTruthy();

    // mennään käyttäjänä maksusivulle
    await userPage.goto(`/maksut/?secret=${invoice.secret}&locale=fi`);

    // käynnistetään käyttäjänä maksuflow
    await userPage.getByRole("link", { name: "Siirry maksamaan" }).click();

    // tämä osuus mokattu, selain palaa automaattisesti takaisin maksut-sovellukseen

    // varmistetaan että ollaan käyttäjänä palattu maksuihin tehdyn maksun sivulle ja maksu merkitty maksetuksi
    await assertInvoiceMarkedPaid(invoice.secret);

    // varmistetaan että kuitit on lähetetty
    await assertEmailsSent(invoice.payerEmail);
  });
});
