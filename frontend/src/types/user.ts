export interface MemberSummary {
  id: number;
  email: string;
  name: string;
  careerLevel: string;
  jobTitle?: string | null;
}
