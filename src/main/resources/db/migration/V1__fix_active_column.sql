-- First, check if the 'active' column exists, then drop it
DO $$ 
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'users' AND column_name = 'active') THEN
        ALTER TABLE users DROP COLUMN active;
    END IF;
END $$;

-- Ensure enabled column exists and is set properly
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                  WHERE table_name = 'users' AND column_name = 'enabled') THEN
        ALTER TABLE users ADD COLUMN enabled BOOLEAN DEFAULT true;
    ELSE
        UPDATE users SET enabled = true WHERE enabled IS NULL;
        ALTER TABLE users ALTER COLUMN enabled SET NOT NULL;
    END IF;
END $$;
