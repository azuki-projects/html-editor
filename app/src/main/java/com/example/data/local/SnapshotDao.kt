package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ProjectSnapshot
import kotlinx.coroutines.flow.Flow

@Dao
interface SnapshotDao {
    @Query("SELECT * FROM project_snapshots WHERE projectId = :projectId ORDER BY timestamp DESC")
    fun getSnapshotsForProject(projectId: Long): Flow<List<ProjectSnapshot>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnapshot(snapshot: ProjectSnapshot): Long

    @Delete
    suspend fun deleteSnapshot(snapshot: ProjectSnapshot)

    @Query("DELETE FROM project_snapshots WHERE projectId = :projectId")
    suspend fun deleteAllSnapshotsForProject(projectId: Long)
}
