import { getToken, removeToken, setToken } from "./localStorageService";
import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";

export const logIn = async (username, password) => {
  const response = await httpClient.post(API.LOGIN, {
    username: username,
    password: password,
  });

  setToken(response.data?.result?.token);

  return response;
};

export const logOut = () => {
  removeToken();
};

export const isAuthenticated = () => {
  return getToken();
};

export const registerAccount = async ({ username, email, password }) => {
  const response = await httpClient.post(API.REGISTER, {
    username: username,
    email: email,
    password: password,
  });

  return response;
};

export const requestPasswordReset = async (email) => {
  const response = await httpClient.post("/identity/auth/forgot-password", {
    email: email,
  });

  return response;
}

export const resetPassword = async (token, newPassword) => {
  const response = await httpClient.post("/identity/auth/reset-password", {
    token: token,
    newPassword: newPassword,
  });

  return response;
}

export const resendVerification = async (email) => {
  const response = await httpClient.post(API.RESEND_OTP, {
    email: email,
  });

  return response;
}

export const verifyUser = async ({ email, otpCode }) => {
  const response = await httpClient.post(API.VERIFY_USER, {
    email: email,
    otpCode: otpCode,
  });

  return response;
}