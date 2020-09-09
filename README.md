# COVNews
## 新闻模块
从https://covid-dashboard.aminer.cn/api/events/list 抓取新冠疫情的相关新闻。分为"News"和"Paper"两类显示，每类对应一个Tab（可以增删）。
使用RecyclerView显示新闻列表，在列表的顶部额外包含一个搜索框（作为列表项），在实现功能的同时兼顾美观性。

## 数据模块
从api https://covid2019-api.herokuapp.com/v2/current 获取全球疫情信息，使用AnyChart(https://github.com/AnyChart/AnyChart-Android) 绘制柱状图展示。
从wikipedia(https://zh.wikipedia.org/wiki/2019冠状病毒病中国大陆疫情) 爬取中国大陆的疫情信息，使用SmartTable(https://github.com/huangyanbin/smartTable)绘制表格展示。

## 新冠图谱模块
从https://innovaapi.aminer.cn/covid/api/v1/pneumonia/entityquery 获取新冠图谱实体。提供查询和展示功能。
