-- Safe Space reference PostgreSQL schema
-- Demo Android build remains local-first; this schema documents the production migration target.

create table users (
  id uuid primary key,
  email text unique not null,
  created_at timestamptz not null default now()
);
create table profiles (
  user_id uuid primary key references users(id) on delete cascade,
  display_name text,
  preferred_language text not null default 'en',
  updated_at timestamptz not null default now()
);
create table consent_settings (
  user_id uuid primary key references users(id) on delete cascade,
  journal_ai boolean not null default false,
  analytics boolean not null default false,
  location boolean not null default false,
  voice_transcription boolean not null default false,
  notifications boolean not null default true,
  support_sharing boolean not null default false,
  updated_at timestamptz not null default now()
);
create table mood_checkins (
  id uuid primary key,
  user_id uuid not null references users(id) on delete cascade,
  mood smallint not null check (mood between 1 and 5),
  note text,
  created_at timestamptz not null default now()
);
create table wellbeing_checkins (
  id uuid primary key,
  user_id uuid not null references users(id) on delete cascade,
  mood smallint check (mood between 1 and 5),
  sleep smallint check (sleep between 1 and 5),
  stress smallint check (stress between 1 and 5),
  energy smallint check (energy between 1 and 5),
  safety smallint check (safety between 1 and 5),
  connection smallint check (connection between 1 and 5),
  created_at timestamptz not null default now()
);
create table journal_entries (
  id uuid primary key,
  user_id uuid not null references users(id) on delete cascade,
  body_ciphertext bytea not null,
  mood text,
  favorite boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);
create table journal_tags (
  journal_id uuid references journal_entries(id) on delete cascade,
  tag text not null,
  primary key (journal_id, tag)
);
create table voice_entries (
  id uuid primary key,
  user_id uuid not null references users(id) on delete cascade,
  storage_key text not null,
  transcript_ciphertext bytea,
  created_at timestamptz not null default now()
);
create table user_baselines (
  user_id uuid primary key references users(id) on delete cascade,
  baseline_json jsonb not null,
  calculated_at timestamptz not null default now()
);
create table wellbeing_scores (
  id uuid primary key,
  user_id uuid not null references users(id) on delete cascade,
  score smallint not null check (score between 0 and 100),
  state text not null,
  inputs_json jsonb not null,
  created_at timestamptz not null default now()
);
create table wellbeing_insights (
  id uuid primary key,
  user_id uuid not null references users(id) on delete cascade,
  explanation text not null,
  contributing_signals jsonb not null,
  created_at timestamptz not null default now()
);
create table trusted_contacts (
  id uuid primary key,
  user_id uuid not null references users(id) on delete cascade,
  name text not null,
  relationship text,
  phone text,
  email text
);
create table safety_plans (
  user_id uuid primary key references users(id) on delete cascade,
  plan_json jsonb not null,
  updated_at timestamptz not null default now()
);
create table support_requests (
  id uuid primary key,
  user_id uuid not null references users(id) on delete cascade,
  type text not null,
  message text,
  status text not null default 'pending',
  created_at timestamptz not null default now()
);
create table appointments (
  id uuid primary key,
  user_id uuid not null references users(id) on delete cascade,
  provider_id uuid,
  scheduled_for timestamptz,
  status text not null,
  created_at timestamptz not null default now()
);
create table favorites (
  id uuid primary key,
  user_id uuid not null references users(id) on delete cascade,
  item_type text not null,
  item_id text not null,
  sort_order integer not null default 0
);
create table memory_capsules (
  id uuid primary key,
  user_id uuid not null references users(id) on delete cascade,
  content_json jsonb not null,
  created_at timestamptz not null default now()
);
create table small_wins (
  id uuid primary key,
  user_id uuid not null references users(id) on delete cascade,
  text text not null,
  created_at timestamptz not null default now()
);
create table admin_users (
  user_id uuid primary key references users(id) on delete cascade,
  role text not null check (role in ('administrator','counsellor','support-worker','supervisor'))
);
create table alert_events (
  id uuid primary key,
  user_id uuid not null references users(id) on delete cascade,
  state text not null,
  reasons jsonb not null,
  created_at timestamptz not null default now()
);
create table audit_logs (
  id uuid primary key,
  actor_id uuid not null references users(id),
  action text not null,
  record_type text,
  record_id uuid,
  created_at timestamptz not null default now()
);
create table ai_conversations (
  id uuid primary key,
  user_id uuid not null references users(id) on delete cascade,
  created_at timestamptz not null default now()
);
create table ai_messages (
  id uuid primary key,
  conversation_id uuid not null references ai_conversations(id) on delete cascade,
  role text not null,
  content_ciphertext bytea not null,
  created_at timestamptz not null default now()
);

create index idx_checkins_user_time on wellbeing_checkins(user_id, created_at desc);
create index idx_journal_user_time on journal_entries(user_id, created_at desc);
create index idx_support_status on support_requests(status, created_at desc);
create index idx_alert_user_time on alert_events(user_id, created_at desc);
create index idx_audit_actor_time on audit_logs(actor_id, created_at desc);
