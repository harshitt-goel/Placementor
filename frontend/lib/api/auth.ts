import api from "./axios";

export interface LoginPayload {
  email: string;
  password: string;
}

export interface SignupPayload {
  email: string;
  password: string;
  full_name: string;
}

export const loginUser = async (
  payload: LoginPayload
) => {
  const formData = new URLSearchParams();

  formData.append("username", payload.email);
  formData.append("password", payload.password);

  const { data } = await api.post(
    "/login",
    formData,
    {
      headers: {
        "Content-Type":
          "application/x-www-form-urlencoded",
      },
    }
  );

  return data;
};

export const signupUser = async (
  payload: SignupPayload
) => {
  const { data } = await api.post(
    "/signup",
    payload
  );

  return data;
};