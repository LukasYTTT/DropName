export interface Env {
  // PROFILES: KVNamespace;
}

export default {
  async fetch(request: Request, env: Env, ctx: ExecutionContext): Promise<Response> {
    const url = new URL(request.url);

    // CORS Headers
    const corsHeaders = {
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Methods": "GET, POST, OPTIONS",
      "Access-Control-Allow-Headers": "Content-Type",
    };

    if (request.method === "OPTIONS") {
      return new Response(null, { headers: corsHeaders });
    }

    // CREATE PROFILE
    if (request.method === "POST" && url.pathname === "/profile") {
      try {
        const body = await request.json();
        const id = crypto.randomUUID().substring(0, 8); // simple short ID
        
        // env.PROFILES.put(id, JSON.stringify(body)); // Uncomment when KV is bound
        
        return new Response(JSON.stringify({ id, success: true, message: "Profile saved (Mocked)" }), {
          headers: { ...corsHeaders, "Content-Type": "application/json" },
        });
      } catch (e) {
        return new Response("Invalid JSON", { status: 400 });
      }
    }

    // GET PROFILE (JSON)
    if (request.method === "GET" && url.pathname.startsWith("/p/")) {
      const id = url.pathname.split("/")[2];
      
      // const profile = await env.PROFILES.get(id); // Uncomment when KV is bound
      const profile = JSON.stringify({ name: "Demo User", id }); // Mock response
      
      if (!profile) {
        return new Response("Profile not found", { status: 404 });
      }
      return new Response(profile, {
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    // GET VCARD
    if (request.method === "GET" && url.pathname.startsWith("/vcard/")) {
      const id = url.pathname.split("/")[2];
      
      // const profileData = await env.PROFILES.get(id); // Uncomment when KV is bound
      // if (!profileData) return new Response("Not found", { status: 404 });
      // const p = JSON.parse(profileData);
      const p = { name: "Demo User", phone: "+49 123 456" }; // Mock data

      const vcard = `BEGIN:VCARD\r
VERSION:3.0\r
FN:${p.name}\r
TEL:${p.phone}\r
END:VCARD\r
`;
      return new Response(vcard, {
        headers: {
          ...corsHeaders,
          "Content-Type": "text/vcard",
          "Content-Disposition": `attachment; filename="contact_${id}.vcf"`,
        },
      });
    }

    return new Response("DropName API Online", {
      headers: { ...corsHeaders, "Content-Type": "text/plain" },
    });
  },
};
