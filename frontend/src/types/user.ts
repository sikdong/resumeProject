export interface MemberSummary {
  id: number;
  email: string;
  name: string;
  careerLevel?: string | null;
  jobTitle?: string | null;
}
