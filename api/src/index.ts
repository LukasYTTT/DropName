export interface Env {
  PROFILES: KVNamespace;
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
        
        await env.PROFILES.put(id, JSON.stringify(body));
        
        return new Response(JSON.stringify({ id, success: true, message: "Profile saved successfully" }), {
          headers: { ...corsHeaders, "Content-Type": "application/json" },
        });
      } catch (e) {
        return new Response("Invalid JSON", { status: 400 });
      }
    }

    // GET PROFILE (JSON or Web page)
    if (request.method === "GET" && url.pathname.startsWith("/p/")) {
      const id = url.pathname.split("/")[2];
      const profileData = await env.PROFILES.get(id);
      
      if (!profileData) {
        return new Response(renderErrorPage("Profile Not Found"), {
          status: 404,
          headers: { ...corsHeaders, "Content-Type": "text/html" },
        });
      }

      const acceptHeader = request.headers.get("Accept") || "";
      if (acceptHeader.includes("text/html")) {
        const p = JSON.parse(profileData);
        return new Response(renderWebProfile(p, id), {
          headers: { ...corsHeaders, "Content-Type": "text/html" },
        });
      } else {
        return new Response(profileData, {
          headers: { ...corsHeaders, "Content-Type": "application/json" },
        });
      }
    }

    // GET VCARD
    if (request.method === "GET" && url.pathname.startsWith("/vcard/")) {
      const id = url.pathname.split("/")[2];
      const profileData = await env.PROFILES.get(id);
      if (!profileData) {
        return new Response("Not found", { status: 404 });
      }
      
      const p = JSON.parse(profileData);
      
      let vcard = `BEGIN:VCARD\r\nVERSION:3.0\r\nFN:${p.name}\r\n`;
      
      for (const field of p.fields) {
        const val = field.value;
        const lbl = field.label.toLowerCase();
        if (lbl.includes("phone") || lbl.includes("telefon")) {
          vcard += `TEL;TYPE=CELL:${val}\r\n`;
        } else if (lbl.includes("email") || lbl.includes("mail")) {
          vcard += `EMAIL;TYPE=INTERNET:${val}\r\n`;
        } else {
          vcard += `URL;TYPE=${field.label}:${val}\r\n`;
          vcard += `NOTE:${field.label}: ${val}\r\n`;
        }
      }
      vcard += `END:VCARD\r\n`;

      return new Response(vcard, {
        headers: {
          ...corsHeaders,
          "Content-Type": "text/vcard; charset=utf-8",
          "Content-Disposition": `attachment; filename="contact_${p.name.replace(/\s+/g, "_")}.vcf"`,
        },
      });
    }

    return new Response("DropName API Online", {
      headers: { ...corsHeaders, "Content-Type": "text/plain" },
    });
  },
};

function renderWebProfile(p: any, id: string): string {
  const profileImageSrc = p.profileImageBase64 
    ? `data:image/jpeg;base64,${p.profileImageBase64}`
    : `https://www.gravatar.com/avatar/00000000000000000000000000000000?d=mp&f=y`;

  const fieldsHtml = p.fields.map((f: any) => {
    let linkUrl = f.value;
    const label = f.label.toLowerCase();
    
    if (label.includes("phone") && !linkUrl.startsWith("tel:")) {
      linkUrl = `tel:${f.value}`;
    } else if (label.includes("email") && !linkUrl.startsWith("mailto:")) {
      linkUrl = `mailto:${f.value}`;
    } else if (label.includes("instagram")) {
      linkUrl = `https://instagram.com/${f.value.replace("@", "")}`;
    } else if (label.includes("tiktok")) {
      linkUrl = `https://tiktok.com/@${f.value.replace("@", "")}`;
    } else if (label.includes("youtube")) {
      linkUrl = f.value.startsWith("http") ? f.value : `https://youtube.com/${f.value}`;
    } else if (label.includes("whatsapp")) {
      linkUrl = `https://wa.me/${f.value.replace(/[^\d]/g, "")}`;
    } else if (!f.value.startsWith("http") && (f.value.includes(".") || label.includes("link"))) {
      linkUrl = `https://${f.value}`;
    }

    return `
      <a href="${linkUrl}" class="field-item" target="_blank" rel="noopener noreferrer">
        <span class="field-label">${f.label}</span>
        <span class="field-value">${f.value}</span>
      </a>
    `;
  }).join("");

  return `<!DOCTYPE html>
<html lang="de">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, viewport-fit=cover">
  <title>${p.name} | DropName</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
  <style>
    * {
      box-sizing: border-box;
      margin: 0;
      padding: 0;
    }
    body {
      background-color: #000000;
      color: #ffffff;
      font-family: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
      min-height: 100vh;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      overflow-x: hidden;
      padding: 24px 16px;
      position: relative;
    }
    
    /* Animated Aurora Background Blobs */
    .aurora-bg {
      position: fixed;
      top: 0;
      left: 0;
      width: 100vw;
      height: 100vh;
      z-index: 1;
      filter: blur(100px);
      opacity: 0.6;
      pointer-events: none;
    }
    .blob {
      position: absolute;
      border-radius: 50%;
      mix-blend-mode: screen;
      animation: move 20s infinite alternate;
    }
    .blob1 {
      top: -10%;
      left: -10%;
      width: 300px;
      height: 300px;
      background: #007AFF;
      animation-duration: 25s;
    }
    .blob2 {
      bottom: -10%;
      right: -10%;
      width: 400px;
      height: 400px;
      background: #8A2BE2;
      animation-duration: 20s;
    }
    .blob3 {
      top: 40%;
      left: 60%;
      width: 250px;
      height: 250px;
      background: #30D158;
      animation-duration: 15s;
    }
    @keyframes move {
      0% { transform: translate(0, 0) scale(1); }
      50% { transform: translate(40px, 60px) scale(1.1); }
      100% { transform: translate(-20px, -40px) scale(0.9); }
    }

    /* Glassmorphic Profile Card */
    .card-container {
      position: relative;
      z-index: 10;
      width: 100%;
      max-width: 350px;
      background: rgba(255, 255, 255, 0.08);
      border: 1px solid rgba(255, 255, 255, 0.15);
      border-radius: 28px;
      backdrop-filter: blur(30px);
      -webkit-backdrop-filter: blur(30px);
      padding: 32px 24px;
      text-align: center;
      box-shadow: 0 20px 40px rgba(0,0,0,0.5);
      animation: fadeIn 0.8s cubic-bezier(0.16, 1, 0.3, 1);
    }
    @keyframes fadeIn {
      from { opacity: 0; transform: translateY(20px); }
      to { opacity: 1; transform: translateY(0); }
    }

    /* Profile Details */
    .profile-img {
      width: 110px;
      height: 110px;
      border-radius: 50%;
      object-fit: cover;
      margin-bottom: 20px;
      border: 2px solid rgba(255, 255, 255, 0.25);
      box-shadow: 0 8px 16px rgba(0,0,0,0.25);
    }
    h1 {
      font-size: 26px;
      font-weight: 700;
      letter-spacing: -0.5px;
      margin-bottom: 24px;
      color: #ffffff;
    }

    /* Fields List */
    .fields-list {
      display: flex;
      flex-direction: column;
      gap: 12px;
      margin-bottom: 32px;
      text-align: left;
    }
    .field-item {
      display: flex;
      flex-direction: column;
      background: rgba(255, 255, 255, 0.05);
      border: 1px solid rgba(255, 255, 255, 0.08);
      border-radius: 16px;
      padding: 12px 16px;
      text-decoration: none;
      color: inherit;
      transition: all 0.2s ease;
    }
    .field-item:hover {
      background: rgba(255, 255, 255, 0.12);
      border-color: rgba(255, 255, 255, 0.2);
      transform: translateY(-2px);
    }
    .field-label {
      font-size: 11px;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.8px;
      color: rgba(255, 255, 255, 0.5);
      margin-bottom: 2px;
    }
    .field-value {
      font-size: 15px;
      font-weight: 500;
      color: #ffffff;
      word-break: break-all;
    }

    /* CTA Button */
    .save-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 100%;
      height: 52px;
      background: #30D158;
      border: none;
      border-radius: 14px;
      color: #ffffff;
      font-size: 16px;
      font-weight: 600;
      text-decoration: none;
      cursor: pointer;
      box-shadow: 0 4px 15px rgba(48, 209, 88, 0.4);
      transition: all 0.2s ease;
    }
    .save-btn:hover {
      transform: scale(1.02);
      box-shadow: 0 6px 20px rgba(48, 209, 88, 0.6);
    }
    .save-btn:active {
      transform: scale(0.98);
    }
    
    .footer {
      position: relative;
      z-index: 10;
      margin-top: 24px;
      font-size: 12px;
      color: rgba(255, 255, 255, 0.4);
      letter-spacing: 0.5px;
    }
  </style>
</head>
<body>
  <div class="aurora-bg">
    <div class="blob blob1"></div>
    <div class="blob blob2"></div>
    <div class="blob blob3"></div>
  </div>

  <div class="card-container">
    <img class="profile-img" src="${profileImageSrc}" alt="Profilbild von ${p.name}">
    <h1>${p.name}</h1>
    
    <div class="fields-list">
      ${fieldsHtml}
    </div>
    
    <a href="/vcard/${id}" class="save-btn">Kontakt speichern</a>
  </div>

  <div class="footer">Erstellt mit DropName</div>
</body>
</html>`;
}

function renderErrorPage(msg: string): string {
  return `<!DOCTYPE html>
<html lang="de">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Fehler | DropName</title>
  <style>
    body {
      background: #000;
      color: #fff;
      font-family: -apple-system, sans-serif;
      display: flex;
      align-items: center;
      justify-content: center;
      height: 100vh;
      margin: 0;
    }
    .box {
      text-align: center;
      padding: 32px;
      border: 1px solid rgba(255,255,255,0.1);
      background: rgba(255,255,255,0.05);
      border-radius: 20px;
    }
    h1 { color: #FF453A; margin-bottom: 8px; }
  </style>
</head>
<body>
  <div class="box">
    <h1>${msg}</h1>
    <p>Dieser Link ist ungültig oder abgelaufen.</p>
  </div>
</body>
</html>`;
}
