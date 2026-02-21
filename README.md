Symbiosis — Cambio Earth Systems Event Socialization Platform

Abstract
The app ‘Symbiosis’ is for Cambio Earth Systems employees who attend the company’s Annual General Meeting (AGM). The AGM is a 1.5–2 day in-person conference held in Vancouver that brings together approximately 600–800 employees from around the world. Because this event gathers employees who rarely meet face-to-face, the app is designed specifically to enhance their experience during this large-scale corporate gathering by improving organization, engagement, and networking opportunities. These employees will use the application to rank breakout session preferences before the event, view their assigned sessions and room locations during the conference, access the event schedule, and navigate the hotel venue using a static map. In addition, they will engage with the app’s gamification features by posting photos, reacting to others’ posts and viewing a live leaderboard that updates as points are earned. Since the users are working professionals in a corporate environment, the app must be intuitive, mobile-friendly, and engaging while maintaining a professional tone. This app will also include event organizers and administrators who have elevated permissions that allow them to create and manage breakout sessions, assign participants to rooms based on ranked preferences, manually override assignments when necessary, upload session and speaker information, manage room logistics, and moderate content. 
App name
Symbiosis
How this problem is currently solved 
To organize and run their current Annual General Meetings, Cambio Earth Systems uses an event management platform called Whova. Similar to the software we will be creating, Whova provides features for both organizers and attendees to streamline event planning and execution. Whova helps organizers with creating automatically generated agenda webpages to keep track of various sessions at the event, creating badges for attendees, simplifying check-ins by syncing to the event app, and generating reports about each event. Additionally, it helps attendees create personalized event agendas and promotes networking with their event software through icebreakers, text & video chats, photo posting, and networking contests. 
Our software will be implementing many similar features, although with a greater focus on socialization aspects such as creating, captioning, and liking posts, participating in networking missions/challenges, and communicating through group chats. In addition to Whova, there are many other event management softwares that help organize and run events, such as Eventsquid, Cvent, and YesEvents. All these platforms offer an extensive list of features (registrations, invitations, speaker and attendee management, payments, reports, etc.), but don’t focus as much on networking and socialization between attendees, which can make it more challenging for attendees to feel engaged in the event. The amount of features can also make it more challenging for both organizers and attendees to use the platform and may have a high learning curve. For example, EventSquid offers training for organizers in order to learn how to use their platform. Our solution to these problems is to focus on the features that matter most for Cambio Earth’s AGM and create an intuitive and simple design that makes organizing and participating in the event more enjoyable.


APIs we plan on making use of:
We plan to use the Google Maps Compute Route Matrix API to provide participants with personalized insights on travel distance and time to the event venue.

Before attending the event, users will be able to view how far the venue is from their location and how long it would take to get there. This will help all event attendees to plan accordingly and arrive at the event on time without having to manually check a map service.

Other than that, we will also make use of REST APIs for all communication between the web client and the server, adhering to standard architectural principles for web services.

How will this project make life better
The project will make life better for Cambio Earth employees by addressing the challenges inherent in large-scale corporate gatherings. Currently, the AGM brings together hundreds of employees who may rarely interact face-to-face. The web application directly enhances the value of this event by focusing on engagement and organization.


Improved Engagement and Networking:


Socialization: The platform promotes networking through features like posting, reacting, and group chats, moving beyond the standard transactional functionality of traditional event apps. This encourages employees to connect, share experiences, and build stronger internal relationships, fostering a sense of community within the global company.
Gamification: The ranking and leaderboard system introduces a fun, competitive element that motivates active participation and content creation, making the overall event experience more dynamic and memorable.


Enhanced Organization and Navigation:


Logistics: The application streamlines the complex logistics of managing 600–800 attendees across multiple breakout sessions. By handling session preference ranking, room assignment, and schedule viewing, it significantly reduces confusion and stress for both organizers and participants.
Access to Information: Attendees gain immediate, personalized access to their assigned sessions, room locations, and the full event schedule, ensuring they can navigate the conference efficiently and arrive at sessions on time.


The project is designed to be educational in a corporate context by facilitating the learning that occurs during the AGM sessions, while also being engaging (rather than purely entertainment) by improving the overall attendee experience and promoting professional networking. Its primary purpose is to transform a large, complex corporate event into a well-organized and highly interactive experience that maximizes the return on investment in the AGM.

Target Audience
The target audience for our web application is Cambio Earth Systems employees who attend the company’s Annual General Meeting (AGM). The AGM is a 1.5–2 day in-person conference held in Vancouver that brings together approximately 600–800 employees. The app is designed to enhance organization, engagement, and networking during this large-scale corporate event from around the world. 
The primary users are AGM participants. They will use the app to rank breakout session preferences before the event, view assigned sessions and room locations, access the schedule, navigate the venue using a static map, and engage with gamification features such as posting photos, reacting to content, chatting, and viewing a live leaderboard. Because users are working professionals, the app must be intuitive, mobile-friendly, and maintain a professional tone. 
A secondary audience includes event organizers and administrators. They will manage breakout sessions, assign participants based on ranked preferences, override assignments when necessary, upload session information, manage logistics, and moderate content. Together, these user groups define the app’s functionality, supporting both attendee engagement and efficient event management.

Does this project have many individual features or one main feature (a possibility with many subproblems)? These are the ‘epics’ of your project.
Our project is not built around just one single feature. Instead, it consists of multiple major features that work together to create a complete event management and engagement platform for Cambio Earth’s Annual General Meeting (AGM). These major features can each be considered an epic, because they represent large functional components that can be broken down into smaller subproblems and tasks.
The application has two primary goals:
To help organizers efficiently manage breakout sessions and event logistics.
To improve attendee engagement, interaction, and networking during the event.
From these goals, we have identified several core epics:
1. Breakout Session Management System (Organizer + Participant Epic)
This is one of the main features of the application. It includes creating sessions, defining time slots, assigning rooms, and managing capacity. Organizers will be able to manually override assignments and adjust participants as needed. On the participant side, users will rank or select preferred breakout sessions before the event. The system will then assign sessions based on preferences and constraints. This epic involves backend logic, database management, role-based permissions, and user interface design.
2. Participant Session Selection and Registration Epic
Participants will be able to browse available sessions, view speaker details, and sign up in advance. They can see their personalized schedule once assignments are finalized. This epic includes preference ranking, schedule viewing, and conflict handling. It directly supports event organization and reduces confusion during the conference.
3. Social Interaction and Chat System Epic
This epic focuses on networking and communication. Users can post messages, react to posts, and participate in chats. The goal is to encourage engagement among employees who may not frequently meet in person. This includes real-time or near-real-time messaging functionality, moderation tools for organizers, and user-generated content management.
4. Photo Sharing and Reactions Epic
Participants will be able to upload photos during the event, react to others’ posts, and interact socially within the app. This adds a gamified and interactive layer to the experience. Organizers will have the ability to moderate content if necessary.
5. Ranking and Leaderboard System Epic (Gamification)
To increase engagement, users will earn points based on participation (posting, reacting, attending sessions, etc.). A live leaderboard will display rankings. This feature introduces additional backend logic for tracking actions and calculating scores.
6. Admin and Content Management Epic
Organizers will have elevated permissions. They can create sessions, upload speaker information, manage room logistics, delete inappropriate posts, override session assignments, and upload event details such as navigation maps. This epic ensures the platform remains structured, secure, and professionally managed.
Overall, our project has many individual features rather than one simple feature. However, these features are unified under one main theme: improving organization and engagement at a large corporate event. Each epic represents an important component of the system and can be assigned as a primary responsibility to individual team members. Because each epic includes backend logic, frontend interface work, and database design, the project scope is substantial and appropriate for a five-person team. 

Is the amount of work required in this proposal sufficient for five group members?
Yes, the amount of work required in this proposal is sufficient for five group members. The project includes several major features as well as multiple supporting and side features that will require significant time for planning, development, testing, and integration. Core components such as chat functionality, session creation, breakout room management with admin override, participant session selection and sign up, photo posting with reactions, and the ranking and leaderboard system each involve both frontend and backend work, database design, and user interaction logic.
In addition to these main features, there are smaller but important tasks such as UI design consistency, validation, error handling, authentication controls, and overall system integration. These side features will also require careful coordination and testing.
While each team member can take primary responsibility for one major feature, everyone will still collaborate across components to ensure smooth integration and consistency throughout the application. This shared approach ensures the workload is balanced, the development process is manageable, and all five members are meaningfully contributing to both major and supporting parts of the project.

Member strengths, background and experiences:
Mitchell: Backend Developer and Frontend Developer
Experience with front-end web development (HTML, CSS, using Figma) 
Experienced in C, C++
Has delved into using Vite and Node.js, ReactJS

Oleksiy (Alyoshka): Full Stack Developer and Project Manager
Experience with writing full-stack web apps using technologies such as ReactJS, Node.js, Express.js, and Next.js 
Used dev tools like Git/GitHub and database management systems like PostgreSQL and MongoDB. 
Comfortable with HTML, CSS, JavaScript, TypeScript, Python, C, and Java.


Tanveen (Tina): Backend Developer and Project Manager
Comfortable with HTML, CSS, C, C++, Python, and UI development tools like Figma
Some experience with JavaScript and Java
Used dev tools like Git/GitHub and database management systems like PostgreSQL and MongoDB

Talha: QA / Testing Analyst and Full Stack Developer
Experienced with HTML, CSS, C++, C, Python
Beginner level Java and Node.js 
UX/UI design (beginner level) 


Ali: QA / Testing Analyst and Frontend developer
Experience with HTML, CSS, Python, C++, and C
A little bit of experience with Java


