# Teaching Assistant Recruitment Management System
A lightweight Java Web system designed for teaching assistant recruitment, realizing the whole process management of job posting, application review, and intelligent skill matching.

---

## 🎯 Project Introduction
Designed for **MO (Recruiters)**, **TA (Job Applicants)** and **Admin (System Administrators)**, this system implements the full digital management of job posting, application review, intelligent skill matching, and workload statistics. Without a database, the system stores data in CSV files, making it simple to deploy and convenient to maintain. It mainly solves the pain points of low efficiency and scattered processes in skill matching between positions and applicants in campus recruitment.

---

## ✅ Implemented Core Features

## Core Implemented Features

### 1. TA Portal - Job Browsing and Intelligent Filtering

**Features:**
1. **Job List View**: After logging in, TAs can view all available positions, with core fields such as subject, work type, skill requirements, weekly working hours, and compensation displayed in a table.
2. **Multi-condition Filtering**: Supports combined filtering by subject and work type, with stackable filter conditions.
3. **Keyword Search**: Supports keyword search for position information, matching text fields including position subject, skill requirements, and job description.
4. **On-demand Detail Expansion**: The list displays concise information by default without expanding job descriptions. Click View Details to expand details row by row, improving information density and browsing efficiency.
5. **Real-time Filter Result Refresh**: Matching results are returned instantly after clicking Search; click Clear to reset all conditions and restore the full position view.
6. **Interface Readability Optimization**: Styled the job filtering page (layout division, button status, row highlighting, etc.) to enhance information readability and user experience.

**Core Algorithms:**
1. **Open Position Filtering Algorithm**: Filters out non-open positions (only retains OPEN status) when traversing the position set to avoid invalid positions entering the candidate result set.
2. **Combined Condition Filtering Algorithm**: Uses subject + workType + keyword as combined conditions to judge each position in the list, including only those that meet all criteria in the result.
3. **Keyword Matching Algorithm**: Performs inclusive matching after unifying the case of keywords and target fields, improving retrieval robustness and matching stability.
4. **Result Set Construction Algorithm**: Builds the filtered result list with a single traversal, and returns the results and filter parameters to the page to ensure consistency between displayed results and filter conditions.
5. **Lazy Detail Display Strategy**: Uses a front-end on-demand expansion mechanism for job descriptions, which are displayed only when triggered by users to reduce redundant information on the initial page.

---

### 2. TA Portal - Job Application and Status Management

**Features:**
1. **Job Application**: After logging in, TAs select open positions, pre-fill application information based on personal profiles, and upload resume files to submit applications. The server forcibly verifies resume upload; upon approval, an application record with an initial status of PENDING is generated and written into application.csv.
2. **Anti-duplicate Submission**: Before submission, the system checks whether a valid application (PENDING / ACCEPTED / REJECTED) already exists for the same TA and the same position; if so, submission is blocked with a prompt.
3. **Application Status Query**: Displays all personal applications in a table, including subject, work type, weekly hours, compensation, job description, and current status.
4. **Position details**: are dynamically queried from job.csv via jobId.
5. **Application Withdrawal**: Withdrawal is only allowed for applications in PENDING status. The server verifies status validity; upon success, the application record is deleted from application.csv.

**Core Algorithms:**
1. **Valid Application Detection Algorithm**: Traverses the application list with taId and jobId as a composite key to determine the existence of records in PENDING / ACCEPTED / REJECTED status for duplicate submission interception and status query.
2. **Unique Application ID Generation Algorithm**: Generates application numbers with the prefix APP plus a millisecond-level timestamp (e.g. APP1744000000000) to ensure record uniqueness.
3. **Resume Isolated Storage Algorithm**: Assigns independent directories in the structure of cvs/{taId}/{jobId}/ to prevent resume overwriting across positions; the path is written into the CSV along with application records.
4. **Application Deletion Algorithm**: Locates records through dual matching of appId and taId, verifies the current status is PENDING, removes the record from the list, and rewrites it back to the CSV file.

---

### 3. TA Portal - Personal Profile Management

**Features:**
1. **Profile Creation and Editing**: Fill in name, ID, email, skills, and major when creating a profile. ID is unique and unchangeable during editing; existing information can be modified, uploaded resumes can be reused, and data is synchronized to TAprofile.csv in real time after operations.
2. **Resume Upload**: Supports PDF resume upload; files are saved to the server directory, with resume paths bound and stored in one-to-one correspondence with profile information.
3. **Profile List View**: View all submitted personal profiles in a table, displaying ID, name, email, skills, major, resume, and other details.
4. **Profile Deletion**: Delete any profile with a confirmation prompt before deletion; TAprofile.csv is updated automatically after deletion, and the page redirects back to the profile list to show the latest data.
5. **Data Persistence**: All profile additions, modifications, and deletions are synchronized to CSV files in real time for persistent storage.

**Core Algorithms:**
1. **CSV Read/Write Algorithm**: Reads, parses, adds, updates, and deletes TA profile data in CSV files, storing data in the standard format of fixed fields (taId,name,email,skills,major,cvPath) to support data persistence and real-time synchronization.
2. **Unique Identifier Judgment Algorithm**: Checks for the existence of a profile record by TAID; updates the record if it exists, and adds a new one if not.
3. **Profile Deletion Algorithm**: Filters and removes corresponding records by ID, then rewrites the data to the CSV file to ensure data consistency.

---

### 4. MO Portal - Job Posting and Hiring

**Features:**
1. **Job Posting Management**: MO users can post positions by entering required information (subject, work type, description, skill requirements, weekly working hours, compensation, max hire count) after identity authentication. A unique position ID is automatically generated using the MO ID plus a 6‑digit alphanumeric string. Position data is persisted to `job.csv` in real time, with the default status set to “OPEN”.
2. **Application Review Management**: View all TA applications belonging to the current MO. Supports hiring applicants (change application status to ACCEPTED and update job status to FILLED if max hire is reached), canceling hire (change application back to PENDING and restore job to OPEN), rejecting applicants (set to REJECTED), and canceling rejection (restore to PENDING). All operations are synchronized to `application.csv` and `job.csv` in real time.
3. **Application List Query**: After authentication, MO users can query all their applications with complete fields including applicant name, TA ID, major, introduction, skills, email, CV path, application ID, job ID, and status (PENDING/ACCEPTED/REJECTED).
4. **Integrated Identity Authentication**: All operations require validation of MO user ID and password based on `auth.csv`. User data is loaded and cached at system startup for efficiency. The system automatically adapts to different deployment paths using ServletContext for file location.

**Core Algorithms:**
1. **Unique ID Generation Algorithm**: Generates a unique job ID by combining the MO ID with a 6‑digit random alphanumeric string to ensure global uniqueness.
2. **CSV File Read/Write and Parsing Algorithm**: Supports escaping and unescaping special characters (commas, line breaks, quotes), parses CSV rows reliably, and enables bidirectional serialization and deserialization between Job/Application objects and CSV files with built‑in fault tolerance.
3. **Status Linkage Update Algorithm**: Synchronizes application status and corresponding job status during hire, cancel hire, reject, and cancel reject operations to maintain consistent data based on the max hire limit. The system performs real‑time statistics of accepted applicants and updates status accordingly.

---

### 5. MO Portal - Application Review and Skill Matching

**Features:**
1. **MO Authentication**: Verifies the validity of MO via ID and password; unauthorized users cannot access application data. Each MO account can only view applications for positions posted by itself.
2. **Application List Display**: Automatically filters all positions posted by the current MO by account. MOs can view applicant ID, position ID, application status, and skill match score in a clear and user-friendly interface.
3. **Skill Match Scoring**: Automatically calculates the matching percentage between TA skills and position requirements based on job skill requirements and TA personal skills, presented as a 100-point score to help MOs quickly select suitable candidates.

**Core Algorithms:**
1. **Profile Loading**: Reads TA application records and MO position records from CSV files at project startup, builds a skill mapping table and TA ID list to avoid repeated I/O and improve performance.
2. **Job Ownership Filtering Algorithm**: Traverses the position list by MO ID to accurately filter all positions posted by the current user for data isolation.
3. **Skill Match Score Calculation Algorithm**: Splits, trims, and standardizes job requirements and TA skills to lowercase, then calculates the percentage score by dividing the number of successful matches by the total number of required skills for the position.

---

### 6. Admin Portal - Position Status Management and Workload Review

**Features:**
1. **Admin Login Authentication**: Admins log in to the system with username and password; unauthorized users cannot access backend pages.
2. **Position Status Management**: Admins can view all position information and perform Open / Close operations, with updates synchronized to job.csv after saving.
3. **TA Workload Review**: Admins can view TA course assignments and total working hours, supporting filtering by course number and status.
4. **Overload Handling**: The system automatically identifies overloaded TAs; admins can mark overload records as Reassigning and save them to workloads.csv.

**Core Algorithms:**
1. **Workload Summarization Algorithm**: Aggregates courseWorkHour for each course by TA id to calculate the total working hours of TAs.
2. **Overload Judgment Algorithm**: Automatically marks TA status as Overloaded when total working hours exceed 15 hours, otherwise Normal.
3. **Status Update Algorithm**: Updates the record status to Reassigning after the admin performs the Reassign operation on an overload record, which is excluded from subsequent statistics to ensure consistency between status and statistical results.
4. **CSV Read/Write Algorithm**: Reads, parses, and saves position and workload data in CSV files, supporting basic field escaping and unescaping.

---

## 🛠️ Tech Stack
- **Backend**: Java Servlet
- **Application Server**: Apache Tomcat 10+
- **Frontend**: JSP + HTML
- **Data Storage**: CSV files
- **Development Tool**: Eclipse

---

## 📂 Project Structure
```
weblogin/
├── .idea/                 # IDE configuration files
├── META‑INF/              # Web metadata
├── WEB‑INF/               # Web application configuration
├── build/                 # Compiled output files
├── data/                  # Persistent CSV data files
│   ├── application.csv    # TA application records
│   ├── auth.csv           # User authentication data
│   ├── job.csv            # Job posting data
│   ├── profiles.csv       # TA personal profile data
│   └── workloads.csv      # TA workload statistics
├── jsp/                   # Front‑end view pages
│   ├── MO_1/              # Module Organizer pages
│   ├── TA/                # TA user pages
│   ├── Ta_Job/            # TA job browsing pages
│   ├── admin/             # Administrator backend pages
│   └── login/             # Unified login pages
└── src/                   # Java backend source code
    ├── Admin/             # Administrator business modules
    ├── MOpublish_apply/   # MO business modules
    ├── TA/                # TA core service modules
    └── TaJob/             # TA job‑related service modules
```
---

## 🚀 Quick Deployment
### Environment Requirements
- JDK 11+
- Apache Tomcat 10.0+
- Eclipse IDE for Enterprise Java Developers

### Deployment Steps
1. Import the project into Eclipse and configure the Tomcat server.
2. Ensure the CSV files in the `web/data/` directory are complete.
3. Start Tomcat and access `http://localhost:8080/weblogin/login/`.
4. Log in with the corresponding role account.

---

## 👥 Team Members (GitHub Username & QMID)
| QMID | GitHub Username |
| ---- | ---- |
| 231222486 | RuqingXu111 (Lead/Member) |
| 231220194 | LQmc-7 (Member) |
| 231220921 | Linyao-Qi (Member) |
| 231222811 | yaoyutong-31 (Member) |
| 231222763 | Star-Angle (Member) |
| 231221881 | KikiGao313 (Member) |

---

## 🤝 Contribution
This project is developed through team collaboration. Future extensions may include TA portal pages, Admin management functions, interface beautification, and other modules.

---

## 📄 Open Source License
This project is open source under the MIT License.
