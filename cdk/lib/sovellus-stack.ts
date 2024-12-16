import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { Nextjs } from 'cdk-nextjs-standalone';
import * as route53 from 'aws-cdk-lib/aws-route53';
import * as acm from 'aws-cdk-lib/aws-certificatemanager';
import { PriceClass } from 'aws-cdk-lib/aws-cloudfront';
import * as shield from 'aws-cdk-lib/aws-shield';

interface MaksutUiStackProps extends cdk.StackProps {
  environmentName: string;
}
export class SovellusStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props: MaksutUiStackProps) {
    super(scope, id, props);

    const publicHostedZones: { [p: string]: string } = {
      hahtuva: 'hahtuvaopintopolku.fi',
      pallero: 'testiopintopolku.fi',
      untuva: 'untuvaopintopolku.fi',
      sade: 'opintopolku.fi',
    };

    const publicHostedZoneIds: { [p: string]: string } = {
      hahtuva: 'Z20VS6J64SGAG9',
      pallero: 'Z175BBXSKVCV3B',
      untuva: 'Z1399RU36FG2N9',
      sade: 'ZNMCY72OCXY4M',
    };

    const zone = route53.HostedZone.fromHostedZoneAttributes(
      this,
      'PublicHostedZone',
      {
        zoneName: `${publicHostedZones[props.environmentName]}.`,
        hostedZoneId: `${publicHostedZoneIds[props.environmentName]}`,
      },
    );

    const certificate = new acm.DnsValidatedCertificate(
      this,
      'SiteCertificate',
      {
        domainName: `maksut-ui.${publicHostedZones[props.environmentName]}`,
        hostedZone: zone,
        region: 'us-east-1', // Cloudfront only checks this region for certificates.
      },
    );

    const nextjs = new Nextjs(this, `${props.environmentName}-maksut-ui`, {
      nextjsPath: '../src/maksut-ui', // relative path from your project root to NextJS
      basePath: '/maksut-ui',
      environment: {
        STANDALONE: 'true',
        MAKSUT_URL: `https://virkailija.${publicHostedZones[props.environmentName]}/maksut/api`,
      },
      domainProps: {
        domainName: `maksut-ui.${publicHostedZones[props.environmentName]}`,
        certificate: certificate,
        hostedZone: zone
      },
      overrides: {
        nextjsDistribution: {
          distributionProps: {
            priceClass: PriceClass.PRICE_CLASS_100
          }
        }
      }
    });

    const protection = new shield.CfnProtection(this, 'DistributionShieldProtection', {
      name: `maksut-ui-${props.environmentName} cloudfront distribution`,
      resourceArn: `arn:aws:cloudfront::${this.account}:distribution/${nextjs.distribution.distributionId}`,
    });

    new cdk.CfnOutput(this, "CloudFrontDistributionDomain", {
      value: nextjs.distribution.distributionDomain,
    });
  }
}