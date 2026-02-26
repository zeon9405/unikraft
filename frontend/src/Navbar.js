import React from 'react';
import { Link, useNavigate } from 'react-router-dom';

function Navbar() {
  // 1. [추가] 페이지 이동을 위한 navigate 함수를 초기화했습니다.
  //    - 기술: React Router (useNavigate)
  const navigate = useNavigate();

  // 2. [추가] 로그아웃 기능을 처리하는 함수를 완성했습니다.
  const handleLogout = () => {
    // 2-1. [추가] 로컬 스토리지에서 토큰 삭제
    //      - 기술: Web API (localStorage)
    localStorage.removeItem('token');

    // 2-2. [추가] 사용자에게 알림 표시
    //      - 기술: Browser API (alert)
    alert('로그아웃 되었습니다.');

    // 2-3. [추가] 메인 페이지로 이동
    //      - 기술: React Router (navigate)
    navigate('/');

    // [중요] 페이지 새로고침 (Navbar 상태 갱신을 위해)
    // - 리액트는 localStorage의 변화를 감지하지 못하므로, 강제로 새로고침하여 Navbar를 다시 그립니다.
    window.location.reload();
  };

  // 3. [추가] 현재 로그인 상태 확인
  //    - 기술: Web API (localStorage)
  //    - 'token'이 있으면 true, 없으면 false가 됩니다.
  const isLoggedIn = !!localStorage.getItem('token');

  return (
    <div style={{ padding: '10px', borderBottom: '1px solid #ccc', marginBottom: '20px', display: 'flex', justifyContent: 'space-between' }}>
      {/* 4. [수정] 공통 메뉴 (항상 보임) */}
      {/*    - 기술: React Router (Link) */}
      <div>
        <Link to="/" style={{ marginRight: '10px', textDecoration: 'none', color: 'black', fontWeight: 'bold' }}>Unikraft</Link>
      </div>

      {/* 5. [추가] 조건부 렌더링 (로그인 여부에 따라 다름) */}
      {/*    - 기술: JavaScript (삼항 연산자) */}
      <div>
        {isLoggedIn ? (
          // 로그인 상태일 때 보여줄 메뉴
          <>
            <span style={{ marginRight: '10px' }}>환영합니다!</span>
            <button onClick={handleLogout} style={{ cursor: 'pointer' }}>로그아웃</button>
          </>
        ) : (
          // 비로그인 상태일 때 보여줄 메뉴
          <>
            <Link to="/login" style={{ marginRight: '10px' }}>로그인</Link>
            <Link to="/signup">회원가입</Link>
          </>
        )}
      </div>
    </div>
  );
}

export default Navbar;