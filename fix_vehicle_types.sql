-- First, check which vehicles have types not matching our enum
SELECT id, brand, model, type FROM vehicle WHERE type NOT IN ('SEDAN', 'SUV', 'HATCHBACK', 'COMPACT', 'ECONOMY', 'MINIVAN', 'LUXURY', 'SPORT');

-- Update any vehicles with unknown types to the ECONOMY type
UPDATE vehicle SET type = 'ECONOMY' 
WHERE type NOT IN ('SEDAN', 'SUV', 'HATCHBACK', 'COMPACT', 'ECONOMY', 'MINIVAN', 'LUXURY', 'SPORT');

-- Verify all vehicles now have valid types
SELECT id, brand, model, type FROM vehicle ORDER BY id;
