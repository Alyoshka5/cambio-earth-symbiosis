# Symbiosis

## Abstract
**Symbiosis** is a web application designed for employees of Cambio Earth Systems who attend the company’s Annual General Meeting (AGM). The AGM is a 1.5–2 day, in-person conference held in :contentReference[oaicite:0]{index=0}, bringing together approximately 600–800 employees from around the world.

Because this event gathers employees who rarely meet face-to-face, Symbiosis is built to enhance organization, engagement, and networking during the conference. Employees will use the app to rank breakout session preferences before the event, view assigned sessions and room locations, access the event schedule, and navigate the venue using a static hotel map.

The app also introduces gamification features, allowing users to post photos, react to others’ content, and view a live leaderboard that updates as points are earned. Since users are working professionals in a corporate environment, the app is designed to be intuitive, mobile-friendly, and engaging while maintaining a professional tone.

In addition to regular attendees, the app supports event organizers and administrators with elevated permissions. These users can create and manage breakout sessions, assign participants to rooms based on ranked preferences, manually override assignments when needed, upload session and speaker information, manage room logistics, and moderate content.

---

## Application Name
**Symbiosis**

---

## Current Solution
Cambio Earth Systems currently uses an event management platform called **Whova** to organize and run its Annual General Meetings. Whova provides tools for organizers such as automatically generated agenda pages, badge creation, simplified check-ins, and event reporting. For attendees, it offers personalized schedules, networking tools, chats, photo posting, and engagement contests.

While Symbiosis will implement many similar features, it places greater emphasis on socialization and engagement. The app focuses on creating, commenting on, and liking posts, participating in networking challenges, and communicating through group chats.

Other event management platforms—such as Eventsquid, Cvent, and YesEvents—offer extensive feature sets including registration, payments, invitations, and reporting. However, these platforms often prioritize logistics over networking and can be complex, resulting in steep learning curves for both organizers and attendees.

Symbiosis addresses these issues by focusing specifically on the needs of Cambio Earth’s AGM. The goal is to provide an intuitive, streamlined experience that emphasizes engagement and ease of use while supporting essential event logistics.

---

## APIs and Technologies
- **Google Maps Compute Route Matrix API**  
  Used to provide participants with travel distance and estimated travel time to the event venue before attending the AGM. This helps users plan their arrival without manually checking external map services.

- **REST APIs**  
  Used for all communication between the web client and server, following standard web service architectural principles.

---

## How This Project Makes Life Better
Symbiosis improves the AGM experience by addressing challenges inherent in large-scale corporate gatherings.

### Improved Engagement and Networking
- **Socialization:**  
  Features such as posting, reacting, and group chats encourage employees to connect, share experiences, and build professional relationships.
- **Gamification:**  
  A points system and live leaderboard motivate participation, making the event more interactive and memorable.

### Enhanced Organization and Navigation
- **Logistics:**  
  The app simplifies managing hundreds of attendees across multiple breakout sessions by handling preference ranking, room assignments, and schedule access.
- **Access to Information:**  
  Attendees receive personalized schedules, room locations, and event details in one place, reducing confusion and stress.

Overall, the project transforms a complex corporate event into a well-organized, engaging, and interactive experience that maximizes the value of the AGM.

---

## Target Audience
The primary audience for Symbiosis is Cambio Earth Systems employees attending the Annual General Meeting. These users will:
- Rank breakout session preferences
- View assigned sessions and room locations
- Access the full event schedule
- Navigate the venue using a static map
- Participate in social and gamification features (posting, reacting, chatting, leaderboard)

A secondary audience includes event organizers and administrators. These users manage sessions, assignments, logistics, content moderation, and overall event structure. Together, both user groups define the full functionality of the application.

---

## Project Scope and Epics
This project consists of multiple major features (epics) that together form a complete event management and engagement platform.

### Core Goals
- Efficiently manage breakout sessions and event logistics
- Improve attendee engagement, interaction, and networking

### Epics
1. **Breakout Session Management System**  
   Session creation, room assignment, capacity management, and admin overrides.
2. **Participant Session Selection and Registration**  
   Preference ranking, personalized schedules, and conflict handling.
3. **Social Interaction and Chat System**  
   Posting, reactions, group chats, and moderation tools.
4. **Photo Sharing and Reactions**  
   Photo uploads, reactions, and content moderation.
5. **Ranking and Leaderboard System (Gamification)**  
   Points tracking and live leaderboard updates.
6. **Admin and Content Management**  
   Session management, logistics, moderation, and event data uploads.

These epics are unified under a single theme: improving organization and engagement at a large corporate event.

---

## Team Capacity
The scope of this project is sufficient for a five-person team. Each major feature requires frontend and backend development, database design, testing, and integration. Smaller but essential tasks—such as UI consistency, authentication, validation, and error handling—further balance the workload.

Each team member can take ownership of a major epic while collaborating across the system to ensure cohesion and quality.

---

## Team Members and Roles

- **Mitchell** — Backend & Frontend Developer  
  Experience with HTML, CSS, Figma, C, C++, Vite, Node.js, and React.

- **Oleksiy (Alyoshka)** — Full Stack Developer & Project Manager  
  Experience with React, Node.js, Express, Next.js, PostgreSQL, MongoDB, Git/GitHub, and multiple programming languages.

- **Tanveen (Tina)** — Backend Developer & Project Manager  
  Experience with HTML, CSS, C, C++, Python, Figma, JavaScript, Java, PostgreSQL, MongoDB, and Git/GitHub.

- **Talha** — QA / Testing Analyst & Full Stack Developer  
  Experience with HTML, CSS, C, C++, Python, beginner Java and Node.js, and UX/UI design.

- **Ali** — QA / Testing Analyst & Frontend Developer  
  Experience with HTML, CSS, Python, C, C++, and beginner Java.

---
