import backendFetch from '@/shared/api/backend';
//currently unused
export async function GET() {
  try {
    const response = await backendFetch('/backend/smoketest', {
      method: 'GET',
    });

    return new Response(response.body, {
      status: response.status,
      headers: response.headers,
    });
  } catch {
    return Response.json(
      { error: 'Spring Boot backend unreachable' },
      { status: 500 },
    );
  }
}
