// MongoDB initialization script for TTL index on raw_test_backups collection
// This script runs automatically when the MongoDB container starts

db = db.getSiblingDB('monitoring_db');

// Check if collection exists, if not create it
if (!db.getCollectionNames().includes('raw_test_backups')) {
    print('Creating raw_test_backups collection...');
    db.createCollection('raw_test_backups');
} else {
    print('raw_test_backups collection already exists');
}

// Create TTL index on receivedAt field
// Documents will be automatically deleted 30 days after creation
// 30 days = 30 * 24 * 60 * 60 = 2,592,000 seconds
try {
    db.raw_test_backups.createIndex(
        { receivedAt: 1 },
        { expireAfterSeconds: 2592000 }
    );
    print('TTL index created successfully on raw_test_backups.receivedAt (30 days expiration)');
} catch (error) {
    print('Error creating TTL index: ' + error);
}

// Optional: Create other useful indexes for performance
try {
    db.raw_test_backups.createIndex({ testOrderId: 1 });
    print('Index created on testOrderId');
} catch (error) {
    print('Index on testOrderId may already exist');
}

try {
    db.raw_test_backups.createIndex({ instrumentId: 1 });
    print('Index created on instrumentId');
} catch (error) {
    print('Index on instrumentId may already exist');
}

print('MongoDB initialization completed for monitoring_db');
