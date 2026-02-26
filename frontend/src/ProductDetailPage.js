import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';

function ProductDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [product, setProduct] = useState(null);
  const [count, setCount] = useState(1);

  useEffect(() => {
    fetch(`/api/products/${id}`)
      .then(response => response.json())
      .then(data => setProduct(data));
  }, [id]);

  const handleOrder = () => {
    // 1. 토큰 확인
    //    - localStorage에서 'token'을 꺼냅니다.
    //    - 토큰이 없으면 로그인이 안 된 상태이므로, 알림을 띄우고 로그인 페이지로 이동시킵니다.
    const token = localStorage.getItem('token');
    if (!token) {
      alert('로그인이 필요합니다.');
      navigate('/login');
      return;
    }

    // 2. 서버에 보낼 데이터 준비
    //    - memberId는 이제 토큰에 들어있으므로, 프론트엔드에서 보낼 필요가 없습니다.
    //    - 상품 ID와 수량만 보냅니다.
    const orderData = {
      productId: product.id,
      count: count,
    };

    // 3. fetch 요청 보내기
    fetch('/api/orders', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`, // 4. Authorization 헤더 추가 (중요!)
      },
      body: JSON.stringify(orderData),
    })
    .then(response => {
      if (response.status === 201) {
        alert('주문이 완료되었습니다.');
        navigate('/');
      } else if (response.status === 403) {
        // 403 Forbidden: 토큰이 만료되었거나 유효하지 않은 경우
        alert('로그인 세션이 만료되었습니다. 다시 로그인해주세요.');
        navigate('/login');
      } else {
        alert('주문에 실패했습니다.');
      }
    });
  };

  if (!product) {
    return <div>로딩 중...</div>;
  }

  return (
    <div>
      <h1>{product.name}</h1>
      <img src={product.imageUrl} alt={product.name} style={{ maxWidth: '300px' }} />
      <p>가격: {product.price}원</p>
      <p>설명: {product.description}</p>
      <p>카테고리: {product.category}</p>

      <hr />

      <div>
        <span>수량: </span>
        <input
          type="number"
          value={count}
          onChange={(e) => setCount(Number(e.target.value))}
          min="1"
        />
      </div>

      <button onClick={handleOrder}>주문하기</button>
      <br />
      <Link to="/">목록으로</Link>
    </div>
  );
}

export default ProductDetailPage;