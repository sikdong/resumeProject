export interface EvaluationSummary {
  id: number;
  score: number;
  evaluatedAt: string;
  comment: string | null;
  memberName: string;
}

export interface EvaluationDto {
  score: number;
  comment: string;
}
