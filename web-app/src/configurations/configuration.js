export const CONFIG = {
  API_GATEWAY: "http://localhost:8888/api/v1",
};

export const API = {
  LOGIN: "/identity/auth/token",
  REGISTER: "/identity/auth/registration",
  VERIFY_USER: "/identity/auth/verify-user",
  RESEND_OTP: "/identity/auth/resend-verification",
  MY_INFO: "/profile/users/my-profile",
  MY_POST: "/post/my-posts",
  CREATE_POST: "/post/create",
  UPDATE_PROFILE: "/profile/users/my-profile",
  UPDATE_AVATAR: "/profile/users/avatar",
  SEARCH_USER: "/profile/users/search",
};
