import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import MainPage from './MainPage';
import ProductDetailPage from './ProductDetailPage';
import LoginPage from './LoginPage';
import SignUpPage from './SignUpPage';
import Navbar from './Navbar'; // 1. [추가] Navbar 컴포넌트 import

function App() {
  return (
    <BrowserRouter>
      {/* 2. [추가] Navbar 컴포넌트 배치 */}
      {/*    - Routes 바깥쪽에 배치해야 페이지가 바뀌어도 Navbar는 그대로 유지됩니다. */}
      <Navbar />

      <Routes>
        {/* 메인 페이지 (상품 목록) */}
        <Route path="/" element={<MainPage />} />

        {/* 상품 상세 페이지 (동적 라우팅) */}
        <Route path="/product/:id" element={<ProductDetailPage />} />

        {/* 로그인 페이지 라우트 추가 */}
        <Route path="/login" element={<LoginPage />} />

        {/* 회원가입 페이지 라우트 추가 */}
        <Route path="/signup" element={<SignUpPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;