import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom'; // <-- IMPORT THIS
import api from '../services/api';
import { Users, Plus, Loader2, Search, Edit, Trash2, ArrowLeft } from 'lucide-react';
import AddStaffModal from '../components/AddStaffModal';

export default function StaffManagement() {
  const navigate = useNavigate(); // <-- INITIALIZE NAVIGATE
  
  const [staffList, setStaffList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editStaff, setEditStaff] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  const fetchStaff = async () => {
    try {
      const response = await api.get('/staff');
      setStaffList(response.data);
    } catch (error) {
      console.error("Failed to fetch staff", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStaff();
  }, []);

  const handleDelete = async (id, name) => {
    if (window.confirm(`Are you sure you want to delete ${name}?`)) {
      try {
        await api.delete(`/staff/${id}`);
        fetchStaff();
      } catch (error) {
        alert("Failed to delete staff member");
      }
    }
  };

  const filteredStaff = staffList.filter(staff => 
    staff.fullName.toLowerCase().includes(searchTerm.toLowerCase()) ||
    staff.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
    staff.roleName.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="space-y-6">
      
      {/* 1. BACK TO DASHBOARD BUTTON */}
      <button 
        onClick={() => navigate('/dashboard')} 
        className="flex items-center gap-2 text-gray-500 hover:text-primary transition font-medium text-sm"
      >
        <ArrowLeft className="h-4 w-4" /> Back to Dashboard
      </button>

      {/* Page Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h2 className="text-3xl font-bold text-gray-800 flex items-center gap-3">
            <div className="bg-blue-100 p-2 rounded-lg">
              <Users className="h-7 w-7 text-primary" />
            </div>
            Staff Management
          </h2>
          <p className="text-gray-500 mt-2 ml-12">Manage hospital staff and their roles.</p>
        </div>
        
        <button 
          onClick={() => { setEditStaff(null); setIsModalOpen(true); }}
          className="flex items-center gap-2 bg-primary hover:bg-blue-700 text-white px-6 py-3 rounded-lg transition shadow-md hover:shadow-lg font-medium"
        >
          <Plus className="h-5 w-5" /> Add New Staff
        </button>
      </div>

      {/* Search Bar */}
      <div className="relative">
        <Search className="absolute left-4 top-3.5 h-5 w-5 text-gray-400" />
        <input 
          type="text" 
          placeholder="Search by name, email, or role..." 
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full pl-12 pr-4 py-3 bg-white border border-gray-200 rounded-xl focus:ring-2 focus:ring-primary focus:border-transparent outline-none shadow-sm"
        />
      </div>

      {/* 2. DATA TABLE (Removed borders and fixed widths) */}
      {loading ? (
        <div className="flex justify-center items-center py-20">
          <Loader2 className="h-8 w-8 text-primary animate-spin" />
        </div>
      ) : filteredStaff.length === 0 ? (
        <div className="bg-white p-12 rounded-2xl text-center text-gray-500">
          <Users className="h-12 w-12 mx-auto mb-3 text-gray-300" />
          <p className="text-lg font-medium">No staff members found.</p>
          <p className="text-sm mt-1">Add a new staff member to get started.</p>
        </div>
      ) : (
        // REMOVED: border border-gray-200 and shadow-sm to remove the "boxed" borderline look
        <div className="bg-white rounded-2xl overflow-hidden">
          {/* Desktop Table */}
          <div className="hidden md:block overflow-x-auto">
            <table className="w-full text-left">
              {/* REMOVED: border-b border-gray-200 from thead for a cleaner look */}
              <thead className="bg-gray-50/50">
                <tr>
                  <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Staff</th>
                  <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Email</th>
                  <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Role</th>
                  <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Department</th>
                  <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Salary</th>
                  <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider text-center">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100"> {/* Changed to lighter divide color */}
                {filteredStaff.map((staff) => (
                  <tr key={staff.id} className="hover:bg-gray-50/50 transition">
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-3">
                        {staff.passportPhoto ? (
                          <img 
                            src={`http://localhost:8081/uploads/${staff.passportPhoto}`} 
                            alt={staff.fullName} 
                            className="h-10 w-10 rounded-full object-cover border-2 border-white shadow-sm"
                            onError={(e) => {
                              e.target.onerror = null;
                              e.target.src = 'data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%239CA3AF"><path d="M24 24h-24v-24h24v24z"/><path fill="%23fff" d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>';
                            }}
                          />
                        ) : (
                          <div className="h-10 w-10 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white text-sm font-bold shadow-sm">
                            {staff.fullName.charAt(0)}
                          </div>
                        )}
                        <div>
                          <p className="font-semibold text-gray-900">{staff.fullName}</p>
                          <p className="text-xs text-gray-500">{staff.staffId}</p>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600">{staff.email}</td>
                    <td className="px-6 py-4">
                      <span className={`px-3 py-1 text-xs font-semibold rounded-full ${
                        staff.roleName === 'Super Admin' ? 'bg-purple-100 text-purple-800' :
                        staff.roleName === 'Hospital Admin' ? 'bg-blue-100 text-blue-800' :
                        'bg-gray-100 text-gray-800'
                      }`}>
                        {staff.roleName}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600">{staff.departmentName || staff.department}</td>
                    <td className="px-6 py-4 text-sm font-medium text-gray-900">{staff.basicSalary?.toLocaleString()}</td>
                    <td className="px-6 py-4">
                      <div className="flex justify-center gap-2">
                        <button 
                          onClick={() => { setEditStaff(staff); setIsModalOpen(true); }} 
                          className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition"
                          title="Edit"
                        >
                          <Edit className="h-4 w-4" />
                        </button>
                        <button 
                          onClick={() => handleDelete(staff.id, staff.fullName)} 
                          className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition"
                          title="Delete"
                        >
                          <Trash2 className="h-4 w-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Mobile Cards (Kept clean and borderless) */}
          <div className="md:hidden divide-y divide-gray-100">
            {filteredStaff.map((staff) => (
              <div key={staff.id} className="p-4 space-y-3">
                <div className="flex items-center gap-3">
                  {staff.passportPhoto ? (
                    <img src={`http://localhost:8081/uploads/${staff.passportPhoto}`} alt={staff.fullName} className="h-12 w-12 rounded-full object-cover border-2 border-white shadow-sm" />
                  ) : (
                    <div className="h-12 w-12 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white font-bold">
                      {staff.fullName.charAt(0)}
                    </div>
                  )}
                  <div className="flex-grow">
                    <p className="font-semibold text-gray-900">{staff.fullName}</p>
                    <p className="text-xs text-gray-500">{staff.staffId}</p>
                  </div>
                  <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
                    staff.roleName === 'Super Admin' ? 'bg-purple-100 text-purple-800' :
                    staff.roleName === 'Hospital Admin' ? 'bg-blue-100 text-blue-800' :
                    'bg-gray-100 text-gray-800'
                  }`}>
                    {staff.roleName}
                  </span>
                </div>
                <div className="text-sm text-gray-600 space-y-1 pl-15">
                  <p>{staff.email}</p>
                  <p>{staff.departmentName || staff.department}</p>
                </div>
                <div className="flex justify-between items-center pt-2">
                  <span className="font-medium text-gray-900">₦{staff.basicSalary?.toLocaleString()}</span>
                  <div className="flex gap-2">
                    <button onClick={() => { setEditStaff(staff); setIsModalOpen(true); }} className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg"><Edit className="h-4 w-4" /></button>
                    <button onClick={() => handleDelete(staff.id, staff.fullName)} className="p-2 text-red-600 hover:bg-red-50 rounded-lg"><Trash2 className="h-4 w-4" /></button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Add/Edit Staff Modal */}
      {isModalOpen && (
        <AddStaffModal 
          onClose={() => { setIsModalOpen(false); setEditStaff(null); }} 
          onSaved={fetchStaff} 
          editData={editStaff} 
        />
      )}
    </div>
  );
}