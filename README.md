# auth-service

**认证授权服务**（Spring Boot 3 + K8s/Istio 网格，无 Spring Cloud 组件）。

## 职责
- OAuth2/JWT 签发与刷新、权限码接口
- 多平台免登换发：钉钉免登码 / 企微 oauth code / 飞书 login code → 统一平台 JWT（所有终端同一种 token）
- 401 统一出口，基座统一拦截

## 部署
Dockerfile + deploy/（Deployment/Service/HPA）随本仓管理；Istio 路由集中在 platform-shell 仓。