import { BrowserRouter, Route, Routes } from 'react-router-dom';
import AppLayout from './components/layout/AppLayout';
import ResumesPage from './pages/ResumesPage';
import MyResumesPage from './pages/MyResumesPage';
import LoginPage from './pages/LoginPage';
import ResumeDetailPage from './pages/ResumeDetailPage';
import { AuthTokenProvider } from './context/AuthTokenContext';

const App = () => (
  <AuthTokenProvider>
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route element={<AppLayout />}>
          <Route path="/" element={<ResumesPage />} />
          <Route path="/my-resumes" element={<MyResumesPage />} />
          <Route path="/resumes/:resumeId" element={<ResumeDetailPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  </AuthTokenProvider>
);

export default App;
