import React, { useState } from 'react';
import './styles/common.css';

interface ResumeUploadProps {
  onUploadSuccess?: () => void;
}

const ResumeUpload: React.FC<ResumeUploadProps> = ({ onUploadSuccess }) => {
  const [file, setFile] = useState<File | null>(null);
  const [fileName, setFileName] = useState('');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0]);
      setFileName(e.target.files[0].name);
    }
  };

  const handleUpload = async () => {
    if (!file) {
      setMessage('파일을 선택해주세요.');
      return;
    }
    setLoading(true);
    setMessage('');
    const reader = new FileReader();
    reader.onloadend = async () => {
      const base64 = reader.result as string;
      try {
        const response = await fetch('/api/resumes/upload', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            fileName: file.name,
            content: base64,
          }),
        });
        if (response.ok) {
          setMessage('업로드 성공!');
          setFile(null);
          setFileName('');
          if (onUploadSuccess) {
            setTimeout(() => {
              onUploadSuccess();
            }, 1500);
          }
        } else {
          setMessage('업로드 실패!');
        }
      } catch (error) {
        setMessage('업로드 중 오류 발생!');
      } finally {
        setLoading(false);
      }
    };
    reader.readAsDataURL(file);
  };

  return (
    <div className="container">
      <div className="card">
        <h2 style={{ marginBottom: '24px', color: 'var(--text-color)' }}>이력서 업로드</h2>
        <div className="input-file">
          <input
            type="file"
            accept="application/pdf"
            onChange={handleFileChange}
            style={{ display: 'none' }}
            id="file-input"
          />
          <label htmlFor="file-input" style={{ cursor: 'pointer' }}>
            {fileName ? (
              <div>
                <p style={{ margin: '0 0 8px 0' }}>선택된 파일:</p>
                <p style={{ margin: '0', color: 'var(--primary-color)' }}>{fileName}</p>
              </div>
            ) : (
              <div>
                <p style={{ margin: '0 0 8px 0' }}>PDF 파일을 선택하세요</p>
                <p style={{ margin: '0', fontSize: '0.9em', color: '#666' }}>
                  또는 여기에 파일을 드래그하세요
                </p>
              </div>
            )}
          </label>
        </div>
        <button
          className="btn btn-primary"
          onClick={handleUpload}
          disabled={loading || !file}
          style={{
            width: '100%',
            marginTop: '16px',
            opacity: loading || !file ? 0.7 : 1,
          }}
        >
          {loading ? '업로드 중...' : '업로드'}
        </button>
        {message && (
          <div
            className={`message ${
              message.includes('성공') ? 'message-success' : 'message-error'
            }`}
          >
            {message}
          </div>
        )}
      </div>
    </div>
  );
};

export default ResumeUpload; 