CREATE TABLE suppliers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    supply_chain_id BIGINT NOT NULL,
    supplier_code VARCHAR(40) NOT NULL UNIQUE,
    name VARCHAR(160) NOT NULL,
    tier INT NOT NULL,
    country VARCHAR(80),
    region VARCHAR(120),
    material_type VARCHAR(80),
    baseline_risk VARCHAR(20) NOT NULL,
    reliability_score DECIMAL(5, 2),
    lead_time_days INT NOT NULL,
    active BOOLEAN NOT NULL,
    dependency_graph JSON,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE materials (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sku VARCHAR(60) NOT NULL UNIQUE,
    name VARCHAR(160) NOT NULL,
    type VARCHAR(80),
    unit_of_measure VARCHAR(30),
    criticality VARCHAR(20) NOT NULL,
    average_daily_demand DECIMAL(14, 2),
    safety_stock_days INT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE risk_events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    supply_chain_id BIGINT NOT NULL,
    title VARCHAR(180) NOT NULL,
    description TEXT,
    location VARCHAR(160),
    type VARCHAR(40) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL,
    exposure_score DOUBLE NOT NULL,
    source VARCHAR(80),
    detected_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    supply_chain_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    supplier_id BIGINT NOT NULL,
    warehouse_code VARCHAR(40) NOT NULL,
    location VARCHAR(120),
    available_quantity DECIMAL(14, 2) NOT NULL,
    reserved_quantity DECIMAL(14, 2) NOT NULL,
    reorder_point DECIMAL(14, 2) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_inventory_material FOREIGN KEY (material_id) REFERENCES materials(id),
    CONSTRAINT fk_inventory_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);
