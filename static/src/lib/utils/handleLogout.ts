export function handleLogout(expired?: boolean) {
  if(expired){
    localStorage.setItem('jwt', JSON.stringify({expired: true}));
  }else{
    localStorage.removeItem('jwt');
  }
  document.cookie = '';
  window.location.href = '/';
}