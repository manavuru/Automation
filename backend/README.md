# ChatFlow AI - Enterprise WhatsApp Automation SaaS Backend

This folder contains the complete, production-ready Node.js, Express, Socket.io, and OpenAI-powered automation engine for **ChatFlow AI**. 

## Core Tech Stack
* **Server Framework:** Node.js + Express.js API Gateway
* **Real-time Channels:** WebSockets utilizing Socket.io (for instant QR code delivery and live chat syncing)
* **WhatsApp Bridge:** `whatsapp-web.js` (or Baileys library) with secure local multi-tenant session persistence
* **AI Replier & sentiment processor:** OpenAI GPT-4 API
* **Database integrations:** Pre-configured for Postgres (via Supabase Client v2) or Firestore (via Admin-SDK)
* **Job Scheduler:** `node-cron` daemon for queue-based scheduled broadcast campaign processing

---

## Direct Deployment Instructions

### Option 1: Render / Railway Deployment (Backend Hosting)
1. Initialize a new Git Repository in this dashboard backend directory.
2. Push your code to your GitHub Account.
3. Login to **Render.com** or **Railway.app** dashboard and click **New Web Service**.
4. Set the build parameters:
   * **Runtime:** `Node`
   * **Build Command:** `npm install`
   * **Start Command:** `npm start`
5. Configure the following environment variables in your deployment dashboard:
   ```env
   PORT=5000
   JWT_SECRET=your_secret_security_token
   OPENAI_API_KEY=sk-proj-yourOpenAiApiKeyHere
   SUPABASE_URL=https://your-supabase-app-id.supabase.co
   SUPABASE_KEY=yourSupabaseServiceRoleOrAnonKey
   ```

### Option 2: Vercel Deployment (Frontend Web Hosting)
* Your custom React/Next.js interface files can be deployed instantly to **Vercel** with automatic edge CDN routing. Make sure to update the environment file:
  ```env
  NEXT_PUBLIC_API_BACKEND_URL=https://your-render-domain-here.com
  ```

---

## Database Schemas & Setup

If you are using PostgreSQL / Supabase, run this schema inside your SQL Editor Console to set up your tables:

```sql
-- Disable row-level security for high-throughput testing, or configure matching policies:
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    plan_tier VARCHAR(100) DEFAULT 'Free',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE contacts (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(100) NOT NULL,
    status VARCHAR(50) DEFAULT 'Active',
    tags TEXT[] DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE automation_rules (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    keyword VARCHAR(255) NOT NULL,
    reply_template TEXT NOT NULL,
    is_ai_enabled BOOLEAN DEFAULT FALSE,
    prompt_type VARCHAR(100) DEFAULT 'Support',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE chat_messages (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    sender_phone VARCHAR(100) NOT NULL,
    sender_name VARCHAR(255) NOT NULL,
    message_text TEXT NOT NULL,
    sentiment_label VARCHAR(50) DEFAULT 'Neutral',
    is_incoming BOOLEAN DEFAULT TRUE,
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE campaigns (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    message_template TEXT NOT NULL,
    scheduled_time TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(100) DEFAULT 'Scheduled', -- 'Scheduled', 'Sending', 'Completed', 'Draft'
    sent_count INT DEFAULT 0,
    total_count INT DEFAULT 0
);
```
