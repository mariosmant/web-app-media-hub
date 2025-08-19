type Props = { targetId: string };

export function SkipLink({ targetId }: Props) {
  return (
    <a
      className="visually-hidden-focusable skip-link btn btn-sm btn-light position-absolute m-2"
      href={`#${targetId}`}
    >
      Skip to main content
    </a>
  );
}
