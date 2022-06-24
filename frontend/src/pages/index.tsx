import type {NextPage} from 'next'
import {useRouter} from 'next/router'
import React, {PropsWithChildren, useState} from "react";
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import Box from '@mui/material/Box';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import {createTheme, ThemeProvider} from '@mui/material/styles';
import axios from "axios";

const Top: NextPage = () => {
  const theme = createTheme();

  const router = useRouter();

  const authGoogle = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    router.push('http://localhost:9082/auth/google');
    // axios.get('http://localhost:9082/auth/google').then((res) => {
    //   console.log(res);
    // })
  };

  return (
    <ThemeProvider theme={theme}>
      <Container component="main" maxWidth="xs">
        <CssBaseline/>
        <Box
          sx={{
            marginTop: 8,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
          }}
        >
          <Avatar sx={{m: 1, bgcolor: 'secondary.main'}}>
            <LockOutlinedIcon/>
          </Avatar>
          <Typography component="h1" variant="h5">
            Google OAuth
          </Typography>
          <Box component="form" onSubmit={authGoogle} noValidate sx={{mt: 1}}>
            <Button
              type="submit"
              fullWidth
              variant="outlined"
              sx={{mt: 3, mb: 2}}
            >
              Login
            </Button>
          </Box>
        </Box>
      </Container>
    </ThemeProvider>
  )
}

export default Top;
