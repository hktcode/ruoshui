WITH "publication" as
( select "p"."puballtables"
       , "p"."oid"
    from "pg_catalog"."pg_publication" p
   WHERE ?::jsonb ?? p.pubname
)
SELECT                                            "t"."oid"::int8 as "relident"
     ,                                        "n"."nspname"::text as "dbschema"
     ,                                        "t"."relname"::text as "relation"
     ,                          "ascii"("t"."relreplident")::int8 as "replchar"
     , (case when "k"."conrelid" is null then 0 else 1 end)::int8 as "attflags"
     ,                                              "a"."attname" as "attrname"
     ,                                       "a"."atttypid"::int8 as "datatype"
     ,                                      "a"."atttypmod"::int8 as "attypmod"
     ,                                             "tn"."nspname" as "tpschema"
     ,                                              "y"."typname" as "typename"
FROM            ( SELECT "t"."oid"
                       , "t"."relname"
                       , "t"."relnamespace"
                       , "t"."relreplident"
                       , "t"."relpersistence"
                       , "t"."relkind"
                  FROM  "pg_catalog"."pg_class" "t"
                  WHERE EXISTS (SELECT * FROM "publication" WHERE puballtables)
                  UNION
                  SELECT "t"."oid"
                       , "t"."relname"
                       , "t"."relnamespace"
                       , "t"."relreplident"
                       , "t"."relpersistence"
                       , "t"."relkind"
                  FROM            "publication" "a"
                       INNER JOIN "pg_catalog"."pg_publication_rel" "r"
                               ON "a"."oid" = "r"."prpubid"
                       INNER JOIN "pg_catalog"."pg_class"       "t"
                               ON "r"."prrelid" = "t"."oid"
                  WHERE     NOT EXISTS (SELECT * FROM "publication" WHERE puballtables)
                        and NOT "a"."puballtables"
                ) t
     INNER JOIN "pg_catalog"."pg_namespace" "n"
             ON "t"."relnamespace" = "n"."oid"
     INNER JOIN "pg_catalog"."pg_attribute" a
             ON "t"."oid" = "a"."attrelid"
     INNER JOIN "pg_catalog"."pg_type" y
             ON "a"."atttypid" = "y"."oid"
     INNER JOIN "pg_catalog"."pg_namespace" tn
             ON "y"."typnamespace" = tn."oid"
      LEFT JOIN ( select     "c"."conrelid" as "conrelid"
                       , "unnest"("conkey") as "conkey"
                  from            "pg_catalog"."pg_constraint" c
                       inner join ( select "c"."conrelid" as "conrelid"
                                         , "min"("c"."oid") as "oid"
                                    from            "pg_catalog"."pg_constraint" "c"
                                         inner join ( select "conrelid" as "conrelid"
                                                           , max("contype") as "contype"
                                                      from "pg_catalog"."pg_constraint"
                                                      where     "contype" in ('p', 'u')
                                                            and "conrelid" <> 0
                                                      group by "conrelid"
                                                    ) m
                                                 on     m."conrelid" = c."conrelid"
                                                    and m."contype" = c."contype"
                                    group by c."conrelid"
                                  ) m
                               on m."oid" = c."oid"
                ) k
             on     a."attrelid" = k."conrelid"
                and a."attnum" = k."conkey"
WHERE     "t"."relpersistence" = 'p'
      and "t"."relkind" in ('r', 'p')
      and "n"."nspname" not in ('information_schema', 'pg_catalog')
      and "n"."nspname" not like 'pg_temp%'
      and "n"."nspname" not like 'pg_toast%'
      and not "a"."attisdropped"
      and "a"."attnum" > 0
ORDER BY "t"."oid", "a"."attnum"
