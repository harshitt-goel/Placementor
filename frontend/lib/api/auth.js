import api from "./axios";

export const loginUser = async (payload) => {
  const formData = new URLSearchParams();

  formData.append("username", payload.email);
  formData.append("password", payload.password);

  const { data } = await api.post("/login", formData, {
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
  });

  return data;
};

export const signupUser = async (payload) => {
  const { data } = await api.post("/signup", payload);

  return data;
};
