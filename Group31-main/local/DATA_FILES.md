# 数据文件说明

本项目所有数据均以 CSV（逗号分隔值）格式存储在 `data/` 目录下。  
共分两类：**静态文件**（随项目预置，人工维护）和**运行时文件**（程序运行后自动生成）。

---

## 目录结构

```
data/
├── jobs.csv          ← 静态 + 运行时（职位总表）
├── ta_profiles.csv   ← 静态（TA 档案库）
├── app.csv           ← 运行时（申请记录）
├── profile.csv       ← 运行时（TA 档案缓存）
└── cvs/              ← 简历 PDF 目录
    ├── TA001/
    ├── TA002/
    ├── TA003/
    └── TA004/
```

---

## 一、jobs.csv — 职位总表

**类型：** 静态预置 + 运行时追加  
**读写方：** 全项目只读（`JobDataLoader`）；MO 发布新职位时追加写入（`MoService.publishJob`）  
**说明：** 既存储项目初始预置的职位，也接收 MO 在运行时发布的新职位。所有新职位直接追加到本文件末尾。

### 列定义（14 列）

| # | 列名 | 类型 | 示例 | 说明 |
|---|------|------|------|------|
| 1 | jobId | String | `J001` | 职位唯一标识；预置数据用 `J001` 格式，MO 发布时自动生成 UUID |
| 2 | title | String | `Lab Assistant for CS101` | 职位名称 |
| 3 | subject | String | `CS101` | 关联课程代码；MO 发布时可为空 |
| 4 | workType | String | `Lab` | 工作类型：`Lab` / `Tutorial` / `Grading`；MO 发布时可为空 |
| 5 | department | String | `Computer Science` | 所属院系；MO 发布时可为空 |
| 6 | description | String | `Assist in programming labs` | 职位描述；MO 发布时可为空 |
| 7 | requirements | String | `Java and Communication` | 任职要求 |
| 8 | openPositions | int | `3` | 招募人数 |
| 9 | deadline | String | `2026-04-15` | 申请截止日期（YYYY-MM-DD）；MO 发布时可为空 |
| 10 | hoursPerWeek | String | `8-10` | 每周工时范围；MO 发布时可为空 |
| 11 | compensation | String | `$18/hour` | 薪酬标准；MO 发布时可为空 |
| 12 | open | boolean | `true` | 是否开放申请：`true` / `false` |
| 13 | moId | String | `MO001` | 发布该职位的 MO 唯一标识 |
| 14 | jobStatus | String | `OPEN` | 职位状态；预置数据无此列（读取时自动兼容），MO 发布后为 `OPEN` |

> **注意：** 预置数据为 13 列（无 jobStatus），程序读取时向下兼容，第 14 列缺失不影响正常解析。  
> 含逗号的字段（description、requirements 等）写入时自动加双引号。

### 示例

```csv
jobId,title,subject,workType,department,description,requirements,openPositions,deadline,hoursPerWeek,compensation,open,moId,jobStatus
J001,Lab Assistant for CS101,CS101,Lab,Computer Science,Assist in programming labs,Java and Communication,3,2026-04-15,8-10,$18/hour,true,MO001,
<uuid>,"Java TA","","","","","会Java",0,"","","",true,"MO001","OPEN"
```

---

## 二、ta_profiles.csv — TA 档案库

**类型：** 静态预置，程序只读不写  
**读写方：** `TAProfileLoader.loadProfilesFromCSV`  
**说明：** 存储系统中所有注册 TA 的基本信息，作为 TA 登录和身份识别的数据源。程序运行不会修改本文件。

### 列定义（4 列）

| # | 列名 | 类型 | 示例 | 说明 |
|---|------|------|------|------|
| 1 | taId | String | `TA001` | TA 唯一标识 |
| 2 | name | String | `Alice Chen` | TA 姓名 |
| 3 | email | String | `alice@uni.edu` | TA 邮箱 |
| 4 | skills | String | `"Java, Data Structures, Python"` | 技能列表，**含逗号须加双引号** |

### 示例

```csv
taId,name,email,skills
TA001,Alice Chen,alice@uni.edu,"Java, Data Structures, Python"
TA002,Bob Li,bob@uni.edu,"Linear Algebra, MATLAB, C++"
```

---

## 三、app.csv — 申请记录（运行时生成）

**类型：** 运行时生成，首次申请时自动创建  
**读写方：** `ApplicationLoader` + `ApplicationService`（申请、撤回、重新申请）；`MoService.acceptApplicant`（录用时更新状态）  
**说明：** 记录所有 TA 的申请历史。同一 TA 对同一职位可以有多条记录（撤回后重新申请），通过 `appStatus` 区分当前状态。

### 列定义（5 列）

| # | 列名 | 类型 | 示例 | 说明 |
|---|------|------|------|------|
| 1 | appId | String | `a3f2c1...` | 申请唯一标识，自动生成 UUID |
| 2 | jobId | String | `J001` | 申请的职位 ID，对应 jobs.csv 中的 jobId |
| 3 | taId | String | `TA001` | 申请人 TA ID，对应 ta_profiles.csv 中的 taId |
| 4 | appStatus | String | `Submitted` | 申请状态（见下表）|
| 5 | cvFilePath | String | `"data/cvs/TA001/resume_v1.pdf"` | 提交的简历文件路径，**始终加双引号** |

### appStatus 取值

| 状态 | 含义 | 触发操作 |
|------|------|---------|
| `Submitted` | 已提交，待审核 | TA 申请职位 |
| `Withdrawn` | 已撤回 | TA 主动撤回申请 |
| `Accepted` | 已录用 | MO 录用申请人 |

### 示例

```csv
appId,jobId,taId,appStatus,cvFilePath
a3f2c1d4...,J001,TA001,Accepted,"data/cvs/TA001/resume_v1.pdf"
b7e9f2a1...,J001,TA001,Withdrawn,"data/cvs/TA001/resume_v2.pdf"
c1d4e5f6...,J002,TA001,Submitted,"data/cvs/TA001/resume_v1.pdf"
```

> 第 1、2 行为同一 TA 对同一职位的两次申请记录（第一次撤回后重新申请），均保留在文件中。

---

## 四、profile.csv — TA 档案缓存（运行时生成）

**类型：** 运行时生成，首次申请时自动创建  
**读写方：** `TAProfileLoader` + `ApplicationService.saveProfileIfAbsent`  
**说明：** 在 TA 首次提交申请时，自动将其档案从 `ta_profiles.csv` 缓存至本文件。格式与 `ta_profiles.csv` 完全相同，但只包含**曾经申请过职位的 TA**。

### 列定义（4 列）

与 `ta_profiles.csv` 完全相同：taId、name、email、skills。

### 与 ta_profiles.csv 的区别

| 对比项 | ta_profiles.csv | profile.csv |
|--------|----------------|-------------|
| 来源 | 项目预置，人工维护 | 程序运行自动生成 |
| 内容范围 | 全部注册 TA | 仅曾申请过职位的 TA |
| 程序是否写入 | 否（只读）| 是（首次申请时追加）|
| 用途 | TA 登录/身份识别 | 申请记录的 TA 信息关联查询 |

---

## CSV 编码规则

所有文件统一遵循以下规则：

1. **编码：** UTF-8
2. **分隔符：** 英文逗号 `,`
3. **引号：** 字段内容含逗号时，整个字段用双引号 `"` 包裹
4. **引号转义：** 字段内容含双引号时，用 `""` 转义（RFC 4180 标准）
5. **空值：** 用空字符串 `""` 表示（不写 `null`）
6. **首行：** 始终为列名表头，程序读取时自动跳过
