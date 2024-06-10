import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { Nextjs } from 'cdk-nextjs-standalone';
import * as route53 from 'aws-cdk-lib/aws-route53';
import * as acm from 'aws-cdk-lib/aws-certificatemanager';

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
    };

    const publicHostedZoneIds: { [p: string]: string } = {
      hahtuva: 'Z20VS6J64SGAG9',
      pallero: 'Z175BBXSKVCV3B',
      untuva: 'Z1399RU36FG2N9',
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
        domainName: `maksut.${publicHostedZones[props.environmentName]}`,
        hostedZone: zone,
        region: 'us-east-1', // Cloudfront only checks this region for certificates.
      },
    );

    const nextjs = new Nextjs(this, 'maksut-ui', {
      nextjsPath: '../src/maksut-ui', // relative path from your project root to NextJS
      basePath: '/maksut',
      environment: {STANDALONE: 'true'},
      domainProps: {domainName: `maksut.${publicHostedZones[props.environmentName]}`, certificate: certificate, hostedZone: zone},
      overrides: { nextjsServer:
          { functionProps: 
              {environment:
                  {MAKSUT_URL: `https://virkailija.${publicHostedZones[props.environmentName]}/maksut/api`}}}}
    });
    new cdk.CfnOutput(this, "CloudFrontDistributionDomain", {
      value: nextjs.distribution.distributionDomain,
    });
  }
}