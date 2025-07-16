-- Check if active column exists and remove it if present
DO $$ 
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns 
              WHERE table_name = 'users' AND column_name = 'active') THEN
        ALTER TABLE users DROP COLUMN active;
    END IF;
END $$;

-- Ensure enabled column exists
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                  WHERE table_name = 'users' AND column_name = 'enabled') THEN
        ALTER TABLE users ADD COLUMN enabled BOOLEAN DEFAULT true;
    END IF;
END $$;

-- Ensure role column exists with proper enum type
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'role_enum') THEN
        CREATE TYPE role_enum AS ENUM ('ROLE_USER', 'ROLE_ADMIN');
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                  WHERE table_name = 'users' AND column_name = 'role') THEN
        ALTER TABLE users ADD COLUMN role role_enum DEFAULT 'ROLE_USER'::role_enum;
    END IF;
END $$;
