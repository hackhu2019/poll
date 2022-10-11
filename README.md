# 投票系统
## 项目发起背景
[左耳听风专栏](https://time.geekbang.org/column/article/8217?screen=full) 正式入门项目

业务上的需求如下：
- 用户只有在登录后，才可以生成投票表单。
- 投票项可以单选，可以多选。
- 其它用户投票后显示当前投票结果（但是不能刷票）。
- 投票有相应的时间，页面上需要出现倒计时。
- 投票结果需要用不同颜色不同长度的横条，并显示百分比和人数。

技术上的需求如下：
- 这回要用 Java Spring Boot 来实现了，然后，后端不返回任何的 HTML，只返回 JSON 数据给前端。
- 由前端的 JQuery 来处理（打算使用 React）并操作相关的 HTML 动态生成在前端展示的页面。
- 前端的页面还要是响应式的，也就是可以在手机端和电脑端有不同的呈现。 
- 这个可以用 Bootstrap 来完成。

如果你有兴趣，还可以挑战以下这些功能。
- 在微信中，通过微信授权后记录用户信息，以防止刷票。
- 可以不用刷页面，就可以动态地看到投票结果的变化。
- Google 一些画图表的 JavaScript 库，然后把图表画得漂亮一些。

项目参考
<https://github.com/callicoder/spring-security-react-ant-design-polls-app>
<https://github.com/XmchxUp/VoteSystem>