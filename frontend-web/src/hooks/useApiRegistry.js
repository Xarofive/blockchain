import { useEffect, useState } from 'react';
import manual from '../config/manual-endpoints.json';

export default function useApiRegistry() {
  const [registry, setRegistry] = useState(null);

  useEffect(() => {
    const base = import.meta.env.VITE_API_BASE_URL;
    fetch(`${base}/v3/api-docs`)
      .then((r) => r.json())
      .then((spec) => {
        const tags = {};
        Object.entries(spec.paths || {}).forEach(([path, methods]) => {
          Object.entries(methods).forEach(([method, info]) => {
            const tag = (info.tags && info.tags[0]) || 'default';
            if (!tags[tag]) tags[tag] = [];
            tags[tag].push({
              path,
              method: method.toUpperCase(),
              parameters: info.parameters || [],
              requestBody: info.requestBody,
            });
          });
        });
        setRegistry({ tags });
      })
      .catch(() => setRegistry(manual));
  }, []);

  return registry;
}
