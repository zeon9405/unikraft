import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom'; // 1. Link 컴포넌트를 import 합니다.

function MainPage() {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    fetch('/api/products')
      .then(response => response.json())
      .then(data => setProducts(data));
  }, []);

  return (
    <div>
      <h1>상품 목록</h1>
      {products.map(product => (
        // 2. 기존 div를 Link 컴포넌트로 감싸줍니다.
        //    - to 속성: 이동할 URL 경로를 동적으로 생성합니다. (예: /product/1)
        //    - key 속성: map 함수를 사용할 때 각 요소를 구분하기 위한 필수 속성입니다.
        //    - style 속성: Link의 기본 스타일(파란색 밑줄)을 제거하여 깔끔하게 보입니다.
        <Link to={`/product/${product.id}`} key={product.id} style={{ textDecoration: 'none', color: 'inherit' }}>
          <div>
            <h2>{product.name}</h2>
            <p>{product.price}원</p>
          </div>
        </Link>
      ))}
    </div>
  );
}

export default MainPage;