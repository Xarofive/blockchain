import useApiRegistry from '../hooks/useApiRegistry';
import EndpointCard from '../components/EndpointCard.jsx';

export default function Dashboard() {
  const registry = useApiRegistry();
  if (!registry) return <div className="p-4">Loading...</div>;
  return (
    <div className="p-4 space-y-6">
      {Object.entries(registry.tags).map(([tag, endpoints]) => (
        <div key={tag}>
          <h2 className="text-xl font-bold mb-2">{tag}</h2>
          <div className="space-y-4">
            {endpoints.map((ep, idx) => (
              <EndpointCard key={idx} endpoint={ep} />
            ))}
          </div>
        </div>
      ))}
    </div>
  );
}
