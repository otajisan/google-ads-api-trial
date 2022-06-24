import Document, {Html, DocumentContext, Head, Main, NextScript} from 'next/document';

class AppDocument extends Document {
  static async getInitialProps(ctx: DocumentContext) {
    const initialProps = await Document.getInitialProps(ctx);

    return initialProps;
  }

  render() {
    return (
      <Html lang='ja' dir='ltr' className={'scroll-smooth'}>
        <Head>
          <meta name='application-name' content='Google Ads API Trial'/>
          <meta name='viewport' content='initial-scale=1, width=device-width'/>
        </Head>
        <Main/>
        <NextScript/>
      </Html>
    );
  }
}

export default AppDocument;