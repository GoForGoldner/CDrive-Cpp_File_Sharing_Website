# CDrive - Web-Based C++ IDE & File Manager

A full-stack C++ IDE and file management web application built with Angular, Spring Boot, PostgreSQL, and Docker. Features real-time code editing, compilation, and execution capabilities with a responsive drag-and-drop interface.

**🔗 [Website Link](https://cdrivecpp.netlify.app/)**
Status: Live! 🎬

**🔗 [GitHub Repository](https://github.com/GoForGoldner/CDrive-Cpp_File_Sharing_Website)**

![CDrive Demo Video](CDriveCurrentDemo.gif)

## 🚀 Features

- **Code Editor**: Integrated Monaco Editor for C++ development with syntax highlighting
<!-- - **File Management**: Drag-and-drop file upload and intuitive file browsing interface -->
- **Real-time Compilation**: Compile and execute C++ files directly in the browser
- **WebSocket Communication**: Real-time updates and communication between client and server
- **Responsive Design**: Mobile-friendly Angular frontend built with TypeScript
- **Containerized Deployment**: Full Docker containerization for seamless scalability

## 🛠️ Tech Stack

- **Frontend**: Angular 19 (TypeScript) with Server-Side Rendering
- **Backend**: Spring Boot (Java) with RESTful APIs
- **Database**: PostgreSQL for file storage and metadata
- **Real-time**: WebSockets with STOMP protocol
- **Containerization**: Docker & Docker Compose
- **Code Editor**: Monaco Editor integration
- **Online Database**: AWS Lightsail for backend
- **Hosting Service**: Netlify for frontend

## 📋 Prerequisites

- Docker
- Docker Compose

## 🏃‍♂️ Running the Application

1. **Clone the repository**
   ```bash
   git clone https://github.com/GoForGoldner/CDrive-Cpp_File_Sharing_Website
   cd CDrive-Cpp_File_Sharing_Website
   ```

2. **Start the application**
   ```bash
   docker-compose up --build
   ```

3. **Access the application**
   - Frontend: http://localhost
   - Backend API: http://localhost:8080
   - Database: PostgreSQL running on port 5432

## 📁 Project Structure

```
├── frontend/              # Angular 19 application
│   ├── src/              # TypeScript source code
│   ├── package.json      # Node.js dependencies
│   └── Dockerfile        # Frontend container configuration
├── backend/              # Spring Boot application
│   ├── src/              # Java source code
│   ├── pom.xml           # Maven dependencies
│   └── Dockerfile        # Backend container configuration
├── database/             # PostgreSQL configuration
└── docker-compose.yml    # Container orchestration
```

## 🔧 Development

The application uses a microservices architecture with:

- Angular frontend serving the user interface and code editor
- Spring Boot backend handling API requests, file operations, and C++ compilation
- PostgreSQL database storing file metadata and user data
- WebSocket connections enabling real-time features

### Local Development Setup

**Frontend development**
```bash
cd frontend
npm install
ng serve
```

**Backend development**
```bash
cd backend
./mvnw spring-boot:run
```

## 🛑 Stopping the Application

```bash
docker-compose down
```