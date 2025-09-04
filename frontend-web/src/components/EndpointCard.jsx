import { useState } from 'react';

export default function EndpointCard({ endpoint }) {
  const { path, method, parameters, requestBody } = endpoint;
  const [inputs, setInputs] = useState({});
  const [body, setBody] = useState('');
  const [response, setResponse] = useState(null);

  const handleChange = (name, value) => {
    setInputs((prev) => ({ ...prev, [name]: value }));
  };

  const execute = async () => {
    let url = path;
    const query = new URLSearchParams();
    parameters?.forEach((p) => {
      const value = inputs[p.name] || '';
      if (p.in === 'path') url = url.replace(`{${p.name}}`, value);
      else if (p.in === 'query') query.append(p.name, value);
    });
    if ([...query].length) url += `?${query.toString()}`;
    const init = { method };
    if (requestBody) {
      try {
        init.headers = { 'Content-Type': 'application/json' };
        init.body = body ? JSON.stringify(JSON.parse(body)) : undefined;
      } catch {
        setResponse({ error: 'Invalid JSON body' });
        return;
      }
    }
    const start = performance.now();
    try {
      const res = await fetch(`${import.meta.env.VITE_API_BASE_URL}${url}`, init);
      const time = performance.now() - start;
      const text = await res.text();
      let data;
      try { data = JSON.parse(text); } catch { data = text; }
      setResponse({ status: res.status, time, data });
    } catch (e) {
      setResponse({ error: e.message });
    }
  };

  return (
    <div className="border rounded p-4 bg-white">
      <div className="font-mono text-sm mb-2">
        <span className="font-bold mr-2">{method}</span>{path}
      </div>
      <div className="space-y-2">
        {parameters?.map((p) => (
          <div key={p.name} className="flex flex-col">
            <label className="text-sm">
              {p.name} {p.required ? '*' : ''} ({p.in})
            </label>
            <input
              className="border p-1 rounded"
              value={inputs[p.name] || ''}
              onChange={(e) => handleChange(p.name, e.target.value)}
            />
          </div>
        ))}
        {requestBody && (
          <div className="flex flex-col">
            <label className="text-sm">Body (JSON)</label>
            <textarea
              className="border p-1 rounded font-mono"
              rows={3}
              value={body}
              onChange={(e) => setBody(e.target.value)}
            />
          </div>
        )}
        <button
          onClick={execute}
          className="bg-blue-600 text-white px-4 py-2 rounded"
        >
          Execute
        </button>
        {response && (
          <pre className="bg-gray-100 p-2 overflow-auto text-xs">
{JSON.stringify(response, null, 2)}
          </pre>
        )}
      </div>
    </div>
  );
}
