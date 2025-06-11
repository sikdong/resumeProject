import React, { useEffect, useState } from 'react';
import { ResumeResponseDto } from '../types/resume';
import '../styles/common.css';

const ResumeList: React.FC = () => {
    const [resumes, setResumes] = useState<ResumeResponseDto[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        fetchResumes();
    }, []);

    const fetchResumes = async () => {
        try {
            const response = await fetch('/api/resumes');
            if (!response.ok) {
                throw new Error('이력서 목록을 불러오는데 실패했습니다.');
            }
            const data = await response.json();
            setResumes(data);
        } catch (err) {
            setError(err instanceof Error ? err.message : '알 수 없는 오류가 발생했습니다.');
        } finally {
            setLoading(false);
        }
    };

    const handleViewResume = (resumeId: number) => {
        window.open(`/api/resumes/file/${resumeId}`, '_blank');
    };

    if (loading) {
        return (
            <div className="container">
                <div className="card" style={{ textAlign: 'center', padding: '40px' }}>
                    <div className="loading-spinner"></div>
                    <p>이력서 목록을 불러오는 중...</p>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="container">
                <div className="message message-error">
                    {error}
                </div>
            </div>
        );
    }

    return (
        <div className="container">
            <div className="card">
                <h2 style={{ marginBottom: '24px', color: 'var(--text-color)' }}>이력서 목록</h2>
                {resumes.length === 0 ? (
                    <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
                        등록된 이력서가 없습니다.
                    </div>
                ) : (
                    <div style={{ display: 'grid', gap: '16px' }}>
                        {resumes.map((resume) => (
                            <div
                                key={resume.id}
                                className="card"
                                style={{
                                    padding: '16px',
                                    marginBottom: '0',
                                    display: 'flex',
                                    justifyContent: 'space-between',
                                    alignItems: 'center',
                                    transition: 'transform 0.2s ease',
                                    cursor: 'pointer',
                                }}
                                onMouseOver={(e) => {
                                    e.currentTarget.style.transform = 'translateY(-2px)';
                                }}
                                onMouseOut={(e) => {
                                    e.currentTarget.style.transform = 'translateY(0)';
                                }}
                            >
                                <div>
                                    <h3 style={{ margin: '0 0 8px 0', color: 'var(--text-color)' }}>
                                        {resume.fileName}
                                    </h3>
                                    <p style={{ margin: '0', color: '#666', fontSize: '0.9em' }}>
                                        업로드 날짜: {new Date(resume.uploadDate).toLocaleDateString()}
                                    </p>
                                </div>
                                <button
                                    className="btn btn-primary"
                                    onClick={() => handleViewResume(resume.id)}
                                    style={{ minWidth: '100px' }}
                                >
                                    보기
                                </button>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default ResumeList; 