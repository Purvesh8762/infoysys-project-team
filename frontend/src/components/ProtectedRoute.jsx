import React from "react";
import { Navigate, Outlet } from "react-router-dom";
import { getUserRole, isAuthenticated } from "../utils/auth";

const ProtectedRoute = ({ allowedRoles }) => {
  const isAuth = isAuthenticated();
  const userRole = getUserRole();

  if (!isAuth) return <Navigate to="/login" replace />;

  if (allowedRoles && !allowedRoles.includes(userRole)) {
    return <div style={{ color: 'red', padding: '20px' }}>Access Denied: You are not authorized.</div>;
  }

  return <Outlet />;
};

export default ProtectedRoute;