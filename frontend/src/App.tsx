import React, { useState } from 'react';
import './App.css';
import './styles/common.css';
import ResumeUpload from './ResumeUpload';
import ResumeList from './components/ResumeList';

function App() {
  const [showUpload, setShowUpload] = useState(false);

  return (
    <div className="App">
      <header style={{ 
        backgroundColor: 'var(--primary-color)', 
        padding: '20px 0',
        boxShadow: 'var(--shadow)',
        marginBottom: '40px'
      }}>
        <div className="container" style={{ 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'center' 
        }}>
          <h1 style={{ 
            margin: 0, 
            color: 'white',
            fontSize: '24px',
            fontWeight: '600'
          }}>
            이력서 관리 시스템
          </h1>
          <button 
            className="btn btn-primary"
            onClick={() => setShowUpload(!showUpload)}
            style={{
              backgroundColor: 'white',
              color: 'var(--primary-color)',
              border: '1px solid var(--primary-color)'
            }}
          >
            {showUpload ? '목록으로 돌아가기' : '이력서 업로드'}
          </button>
        </div>
      </header>
      <main>
        {showUpload ? (
          <ResumeUpload onUploadSuccess={() => setShowUpload(false)} />
        ) : (
          <ResumeList />
        )}
      </main>
      <footer style={{
        marginTop: '60px',
        padding: '20px 0',
        backgroundColor: 'var(--secondary-color)',
        color: 'var(--text-color)',
        textAlign: 'center'
      }}>
        <div className="container">
          <p style={{ margin: 0 }}>© 2024 이력서 관리 시스템. All rights reserved.</p>
        </div>
      </footer>
    </div>
  );
}

export default App;
