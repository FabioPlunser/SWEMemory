function l(){}function z(t){return t()}function A(){return Object.create(null)}function m(t){t.forEach(z)}function q(t){return typeof t=="function"}function L(t,e){return t!=t?e==e:t!==e||t&&typeof t=="object"||typeof t=="function"}function O(t){return Object.keys(t).length===0}function B(t,...e){if(t==null)return l;const n=t.subscribe(...e);return n.unsubscribe?()=>n.unsubscribe():n}function X(t,e,n){t.$$.on_destroy.push(B(e,n))}function Y(t,e,n){return t.set(n),e}function Z(t,e){t.appendChild(e)}function tt(t,e,n){t.insertBefore(e,n||null)}function M(t){t.parentNode&&t.parentNode.removeChild(t)}function et(t,e){for(let n=0;n<t.length;n+=1)t[n]&&t[n].d(e)}function nt(t){return document.createElement(t)}function T(t){return document.createTextNode(t)}function st(){return T(" ")}function rt(t,e,n,r){return t.addEventListener(e,n,r),()=>t.removeEventListener(e,n,r)}function ot(t,e,n){n==null?t.removeAttribute(e):t.getAttribute(e)!==n&&t.setAttribute(e,n)}function ut(t){return t===""?null:+t}function F(t){return Array.from(t.childNodes)}function it(t,e){e=""+e,t.wholeText!==e&&(t.data=e)}function ct(t,e){t.value=e==null?"":e}function ft(t,e,n){t.classList[n?"add":"remove"](e)}let p;function g(t){p=t}function P(){if(!p)throw new Error("Function called outside component initialization");return p}function at(t){P().$$.on_mount.push(t)}const h=[],k=[],$=[],v=[],D=Promise.resolve();let w=!1;function G(){w||(w=!0,D.then(C))}function E(t){$.push(t)}const x=new Set;let b=0;function C(){const t=p;do{for(;b<h.length;){const e=h[b];b++,g(e),H(e.$$)}for(g(null),h.length=0,b=0;k.length;)k.pop()();for(let e=0;e<$.length;e+=1){const n=$[e];x.has(n)||(x.add(n),n())}$.length=0}while(h.length);for(;v.length;)v.pop()();w=!1,x.clear(),g(t)}function H(t){if(t.fragment!==null){t.update(),m(t.before_update);const e=t.dirty;t.dirty=[-1],t.fragment&&t.fragment.p(t.ctx,e),t.after_update.forEach(E)}}const y=new Set;let a;function lt(){a={r:0,c:[],p:a}}function dt(){a.r||m(a.c),a=a.p}function J(t,e){t&&t.i&&(y.delete(t),t.i(e))}function _t(t,e,n,r){if(t&&t.o){if(y.has(t))return;y.add(t),a.c.push(()=>{y.delete(t),r&&(n&&t.d(1),r())}),t.o(e)}else r&&r()}function ht(t){t&&t.c()}function K(t,e,n,r){const{fragment:o,after_update:_}=t.$$;o&&o.m(e,n),r||E(()=>{const f=t.$$.on_mount.map(z).filter(q);t.$$.on_destroy?t.$$.on_destroy.push(...f):m(f),t.$$.on_mount=[]}),_.forEach(E)}function Q(t,e){const n=t.$$;n.fragment!==null&&(m(n.on_destroy),n.fragment&&n.fragment.d(e),n.on_destroy=n.fragment=null,n.ctx=[])}function R(t,e){t.$$.dirty[0]===-1&&(h.push(t),G(),t.$$.dirty.fill(0)),t.$$.dirty[e/31|0]|=1<<e%31}function gt(t,e,n,r,o,_,f,u=[-1]){const i=p;g(t);const s=t.$$={fragment:null,ctx:[],props:_,update:l,not_equal:o,bound:A(),on_mount:[],on_destroy:[],on_disconnect:[],before_update:[],after_update:[],context:new Map(e.context||(i?i.$$.context:[])),callbacks:A(),dirty:u,skip_bound:!1,root:e.target||i.$$.root};f&&f(s.root);let N=!1;if(s.ctx=n?n(t,e.props||{},(c,S,...I)=>{const j=I.length?I[0]:S;return s.ctx&&o(s.ctx[c],s.ctx[c]=j)&&(!s.skip_bound&&s.bound[c]&&s.bound[c](j),N&&R(t,c)),S}):[],s.update(),N=!0,m(s.before_update),s.fragment=r?r(s.ctx):!1,e.target){if(e.hydrate){const c=F(e.target);s.fragment&&s.fragment.l(c),c.forEach(M)}else s.fragment&&s.fragment.c();e.intro&&J(t.$$.fragment),K(t,e.target,e.anchor,e.customElement),C()}g(i)}class pt{$destroy(){Q(this,1),this.$destroy=l}$on(e,n){if(!q(n))return l;const r=this.$$.callbacks[e]||(this.$$.callbacks[e]=[]);return r.push(n),()=>{const o=r.indexOf(n);o!==-1&&r.splice(o,1)}}$set(e){this.$$set&&!O(e)&&(this.$$.skip_bound=!0,this.$$set(e),this.$$.skip_bound=!1)}}const d=[];function U(t,e=l){let n;const r=new Set;function o(u){if(L(t,u)&&(t=u,n)){const i=!d.length;for(const s of r)s[1](),d.push(s,t);if(i){for(let s=0;s<d.length;s+=2)d[s][0](d[s+1]);d.length=0}}}function _(u){o(u(t))}function f(u,i=l){const s=[u,i];return r.add(s),r.size===1&&(n=e(o)||l),u(t),()=>{r.delete(s),r.size===0&&(n(),n=null)}}return{set:o,update:_,subscribe:f}}const V=localStorage.getItem("loggedIN"),W=U(V||!1);W.subscribe(t=>{console.log("loggedIN",t),localStorage.setItem("loggedIN",t!==!1)});export{ft as A,ut as B,pt as S,st as a,ot as b,tt as c,Z as d,nt as e,it as f,M as g,X as h,gt as i,W as j,ht as k,rt as l,K as m,l as n,at as o,J as p,dt as q,m as r,L as s,T as t,_t as u,Q as v,et as w,lt as x,Y as y,ct as z};
