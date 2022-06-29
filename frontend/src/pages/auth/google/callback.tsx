import {GetServerSidePropsContext, NextPage} from "next";
import axios from "axios";

const Authenticated: NextPage = (p) => {
  return (
    <div>
      <h1>Authenticated</h1>
    </div>
  );
};

export const getServerSideProps = async (ctx: GetServerSidePropsContext) => {
  console.log(ctx);
  const query = ctx.query;
  console.log(query);

  const queryStr = Object.keys(query).map(key => key + '=' + query[key]).join('&');

  const url = 'http://localhost:9082/auth/google/token';
  console.log(url)

  const headers = {'Content-Type': 'application/json'}

  const code = query['code'];
  const body = {
    code: code
  }

  axios.post(url, body, {headers: headers}).then((res) => {
    console.log('==========================================================');
    console.log(res);
    console.log('==========================================================');
  });

  return {props: {}}


  // return {
  //   redirect: {
  //     permanent: false,
  //     destination: url,
  //   }
  // }
};

export default Authenticated;