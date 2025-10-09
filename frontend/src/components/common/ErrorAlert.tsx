const ErrorAlert = ({ message }: { message: string }) => (
  <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600">
    {message}
  </div>
);

export default ErrorAlert;
