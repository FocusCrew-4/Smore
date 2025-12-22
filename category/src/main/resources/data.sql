CREATE TABLE p_categories (
                              id UUID PRIMARY KEY,
                              name VARCHAR(255) NOT NULL UNIQUE,
                              description TEXT,
                              created_at TIMESTAMP NOT NULL,
                              updated_at TIMESTAMP NOT NULL,
                              deleted_at TIMESTAMP,
                              deleted_by BIGINT
);

INSERT INTO p_categories (
    id,
    name,
    description,
    created_at,
    updated_at,
    deleted_at,
    deleted_by
)
VALUES
    (
        '11111111-1111-1111-1111-111111111111',
        'DESSERT',
        '케이크, 쿠키, 마카롱',
        NOW(),
        NOW(),
        NULL,
        NULL
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        'GOODS',
        '굿즈 상품',
        NOW(),
        NOW(),
        NULL,
        NULL
    ),
    (
        '33333333-3333-3333-3333-333333333333',
        'LIMITED',
        '한정 판매 상품',
        NOW(),
        NOW(),
        NULL,
        NULL
    );

