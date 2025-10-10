import { Dialog, Transition } from '@headlessui/react';
import { Fragment, useState } from 'react';
import { uploadResume } from '../../api/resumes';
import type { ResumeUploadPayload } from '../../types/resume';
import ErrorAlert from '../common/ErrorAlert';

interface UploadResumeDialogProps {
  open: boolean;
  onClose: () => void;
  onSuccess?: () => void;
}

const UploadResumeDialog = ({ open, onClose, onSuccess }: UploadResumeDialogProps) => {
  const [title, setTitle] = useState('');
  const [comment, setComment] = useState('');
  const [isMailSent, setIsMailSent] = useState(false);
  const [file, setFile] = useState<File | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const resetForm = () => {
    setTitle('');
    setComment('');
    setIsMailSent(false);
    setFile(null);
    setError(null);
  };

  const handleSubmit = async () => {
    if (!file) {
      setError('PDF 파일을 선택해주세요.');
      return;
    }
    setIsSubmitting(true);
    setError(null);

    const payload: ResumeUploadPayload = {
      title,
      comment,
      isMailSent,
      file,
    };

    try {
      await uploadResume(payload);
      onSuccess?.();
      resetForm();
      onClose();
    } catch (err) {
      const message = err instanceof Error ? err.message : '이력서 업로드에 실패했습니다.';
      setError(message);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Transition show={open} as={Fragment}>
      <Dialog onClose={onClose} className="relative z-50">
        <Transition.Child
          as={Fragment}
          enter="ease-out duration-200"
          enterFrom="opacity-0"
          enterTo="opacity-100"
          leave="ease-in duration-150"
          leaveFrom="opacity-100"
          leaveTo="opacity-0"
        >
          <div className="fixed inset-0 bg-slate-900/40" aria-hidden="true" />
        </Transition.Child>

        <div className="fixed inset-0 overflow-y-auto">
          <div className="flex min-h-full items-center justify-center p-4">
            <Transition.Child
              as={Fragment}
              enter="ease-out duration-200"
              enterFrom="opacity-0 scale-95"
              enterTo="opacity-100 scale-100"
              leave="ease-in duration-150"
              leaveFrom="opacity-100 scale-100"
              leaveTo="opacity-0 scale-95"
            >
              <Dialog.Panel className="w-full max-w-2xl rounded-3xl bg-white p-6 shadow-2xl">
                <Dialog.Title className="text-lg font-semibold text-slate-900">이력서 업로드</Dialog.Title>
                <Dialog.Description className="mt-1 text-sm text-slate-500">
                  PDF 파일과 기본 정보를 입력하면 이력서를 등록할 수 있습니다.
                </Dialog.Description>

                <div className="mt-6 space-y-4">
                  <div>
                    <label className="text-xs font-semibold uppercase tracking-wide text-slate-500" htmlFor="resume-title">
                      제목
                    </label>
                    <input
                      id="resume-title"
                      type="text"
                      value={title}
                      onChange={(event) => setTitle(event.target.value)}
                      placeholder="예) 백엔드 개발자 이력서"
                      className="mt-1 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm text-slate-700 shadow-sm focus:border-brand"
                    />
                  </div>

                  <div>
                    <label className="text-xs font-semibold uppercase tracking-wide text-slate-500" htmlFor="resume-comment">
                      소개 문구
                    </label>
                    <textarea
                      id="resume-comment"
                      value={comment}
                      onChange={(event) => setComment(event.target.value)}
                      placeholder="이력서에 대해 간단히 소개해 주세요."
                      rows={3}
                      className="mt-1 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm text-slate-700 shadow-sm focus:border-brand"
                    />
                  </div>

                  <div className="grid gap-4 sm:grid-cols-2">
                    <div>
                      <label className="text-xs font-semibold uppercase tracking-wide text-slate-500" htmlFor="resume-file">
                        PDF 파일
                      </label>
                      <input
                        id="resume-file"
                        type="file"
                        accept="application/pdf"
                        onChange={(event) => setFile(event.target.files?.[0] ?? null)}
                        className="file:cursor-pointer mt-1 block w-full cursor-pointer rounded-lg border border-dashed border-slate-300 bg-slate-50 px-3 py-2 text-sm text-slate-700 file:mr-4 file:rounded-lg file:border-0 file:bg-brand file:px-3 file:py-2 file:font-semibold file:text-white hover:border-brand"
                      />
                    </div>
                    <label className="mt-6 flex items-center gap-3 rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-600 shadow-inner">
                      <input
                        type="checkbox"
                        checked={isMailSent}
                        onChange={(event) => setIsMailSent(event.target.checked)}
                        className="h-4 w-4 rounded border-slate-300 text-brand focus:ring-brand"
                      />
                      평가 등록 시 이메일 알림 전송
                    </label>
                  </div>

                  {error ? <ErrorAlert message={error} /> : null}
                </div>

                <div className="mt-6 flex justify-end gap-2">
                  <button
                    type="button"
                    onClick={() => {
                      resetForm();
                      onClose();
                    }}
                    className="rounded-lg border border-slate-200 px-4 py-2 text-sm font-semibold text-slate-600 hover:bg-slate-100"
                    disabled={isSubmitting}
                  >
                    취소
                  </button>
                  <button
                    type="button"
                    onClick={handleSubmit}
                    disabled={isSubmitting}
                    className="rounded-lg bg-brand px-4 py-2 text-sm font-semibold text-white shadow-soft transition hover:bg-brand-dark disabled:cursor-not-allowed disabled:bg-slate-300"
                  >
                    {isSubmitting ? '업로드 중...' : '이력서 등록'}
                  </button>
                </div>
              </Dialog.Panel>
            </Transition.Child>
          </div>
        </div>
      </Dialog>
    </Transition>
  );
};

export default UploadResumeDialog;
