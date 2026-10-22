// 공통 스크립트 (CSRF 토큰 포함 AJAX, 필터, 장바구니 금액 계산, 모의 결제)
const csrf = {
  header: document.querySelector('meta[name="_csrf_header"]')?.content,
  token: document.querySelector('meta[name="_csrf"]')?.content,
};

// 플래시 메시지 자동 숨김
document.querySelectorAll('.flash').forEach(el => setTimeout(() => el.style.opacity = 0, 2200));

function toast(msg, error) {
  const el = document.createElement('div');
  el.className = 'flash' + (error ? ' error' : '');
  el.textContent = msg;
  document.body.appendChild(el);
  setTimeout(() => el.style.opacity = 0, 1800);
  setTimeout(() => el.remove(), 2400);
}

// 상품 목록: 필터 폼 값 변경 후 제출
function setAndSubmit(name, value) {
  const form = document.getElementById('filterForm');
  const input = form.querySelector(`input[name="${name}"]`);
  input.value = value;
  if (name !== 'page') form.querySelector('input[name="page"]').value = 0;
  form.submit();
}

// F-11 찜 토글
async function toggleWish(btn, productId) {
  const res = await fetch(`/wish/${productId}`, { method: 'POST', headers: { [csrf.header]: csrf.token } });
  if (res.redirected || !res.headers.get('content-type')?.includes('json')) {
    location.href = '/member/login';
    return;
  }
  const data = await res.json();
  btn.textContent = data.wished ? '♥' : '♡';
  toast(data.wished ? '찜 목록에 추가했습니다 ♥' : '찜이 해제되었습니다');
}

// 상세 수량 버튼
function stepQty(id, d) {
  const el = document.getElementById(id);
  el.value = Math.max(1, (parseInt(el.value) || 1) + d);
  const price = parseInt(el.dataset.price || 0);
  const total = document.getElementById('detailTotal');
  if (total) total.textContent = (price * el.value).toLocaleString('ko-KR') + '원';
}

// F-01 이메일 중복 확인
async function checkEmail() {
  const email = document.getElementById('email').value.trim();
  const out = document.getElementById('emailMsg');
  if (!email) { out.textContent = '이메일을 입력하세요.'; return; }
  const res = await fetch('/member/check-email?email=' + encodeURIComponent(email));
  const data = await res.json();
  out.textContent = data.available ? '사용 가능한 이메일입니다.' : '이미 가입된 이메일입니다.';
  out.style.color = data.available ? 'var(--g)' : 'var(--terra)';
}

// 장바구니: 선택 상품 기준 결제 예정 금액 계산 (3만원 이상 무료배송)
function recalcCart() {
  const checks = [...document.querySelectorAll('.cart-check')];
  if (!checks.length) return;
  const sum = checks.filter(c => c.checked).reduce((a, c) => a + parseInt(c.dataset.price), 0);
  const fee = sum === 0 || sum >= 30000 ? 0 : 3000;
  const fmt = n => n.toLocaleString('ko-KR') + '원';
  document.getElementById('sumProduct').textContent = fmt(sum);
  document.getElementById('sumFee').textContent = fmt(fee);
  document.getElementById('sumTotal').textContent = fmt(sum + fee);
  const all = document.getElementById('checkAll');
  if (all) all.checked = checks.every(c => c.checked);
}
function checkAll(el) {
  document.querySelectorAll('.cart-check').forEach(c => c.checked = el.checked);
  recalcCart();
}
function deleteSelected() {
  const form = document.getElementById('deleteForm');
  const ids = [...document.querySelectorAll('.cart-check:checked')].map(c => c.value);
  if (!ids.length) { toast('삭제할 상품을 선택하세요', true); return; }
  ids.forEach(id => {
    const i = document.createElement('input');
    i.type = 'hidden'; i.name = 'cartItemIds'; i.value = id;
    form.appendChild(i);
  });
  form.submit();
}
function orderSelected(e) {
  if (!document.querySelectorAll('.cart-check:checked').length) {
    e.preventDefault();
    toast('주문할 상품을 선택하세요', true);
  }
}
document.addEventListener('DOMContentLoaded', recalcCart);

// 주문서: 기본 배송지 불러오기
function loadAddress(btn) {
  const d = btn.dataset;
  ['receiverName', 'receiverPhone', 'zipcode', 'address', 'addressDetail'].forEach(k => {
    const el = document.getElementById(k);
    if (el && d[k]) el.value = d[k];
  });
  toast('기본 배송지를 불러왔습니다');
}

// 주문서: 모의 결제 진행 화면 후 제출
function mockPay(form) {
  if (form.dataset.paying) return true;
  const modal = document.getElementById('payModal');
  const method = form.querySelector('input[name="paymentMethod"]:checked');
  document.getElementById('payMethod').textContent = method ? method.dataset.label : '';
  modal.classList.add('show');
  form.dataset.paying = '1';
  setTimeout(() => form.submit(), 1400);
  return false;
}
