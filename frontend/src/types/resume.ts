export interface ResumeResponseDto {
    id: number;
    fileName: string;
    uploadDate: string;
    memberId: number;
}

export interface ResumeUploadRequestDto {
    fileName: string;
    content: string;
} 