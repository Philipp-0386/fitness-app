import { ErrorCode } from './error-code';

export class ApiError extends Error {
  status: number;
  code: ErrorCode;
  path: string | null;
  fieldErrors: Record<string, string> | null;
  timestamp: string | null;

  constructor(params: {
    status: number;
    code: ErrorCode;
    message: string;
    path?: string | null;
    fieldErrors?: Record<string, string> | null;
    timestamp?: string | null;
  }) {
    super(params.message);
    this.status = params.status;
    this.code = params.code;
    this.path = params.path ?? null;
    this.fieldErrors = params.fieldErrors ?? null;
    this.timestamp = params.timestamp ?? null;
  }
}
