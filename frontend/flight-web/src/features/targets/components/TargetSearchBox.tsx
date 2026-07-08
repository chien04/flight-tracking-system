import { Search } from 'lucide-react';
import { FormEvent, useState } from 'react';

import { formatTargetId } from '../../../shared/utils/formatTargetId';

interface TargetSearchBoxProps {
  onSelect: (targetId: string) => void;
}

export function TargetSearchBox({ onSelect }: TargetSearchBoxProps) {
  const [value, setValue] = useState('');

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (value.trim().length === 0) {
      return;
    }
    onSelect(formatTargetId(value));
  }

  return (
    <form className="target-search" onSubmit={handleSubmit}>
      <Search aria-hidden="true" size={18} />
      <input
        aria-label="Target ID"
        inputMode="numeric"
        maxLength={4}
        onChange={(event) => setValue(event.target.value.replace(/\D/g, ''))}
        placeholder="0000"
        value={value}
      />
      <button type="submit">Find</button>
    </form>
  );
}
