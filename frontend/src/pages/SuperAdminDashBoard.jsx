import { useState, useEffect } from 'react';
import api from '../services/api';
import { Building2, Users, TrendingUp, AlertCircle, ArrowLeft } from 'lucide-react';

export default function SuperAdminDashboard() {
  const [hospitals, setHospitals] = useState([]);
  const [stats, setStats] = useState({ totalHospitals: 0, activeHospitals: 0, suspendedHospitals: 0 });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const res = await api.get('/admin/hospitals');
        setHospitals(res.data);
        setStats({
          totalHospitals: res.data.length,
          activeHospitals: res.data.filter(h => h.isActive).length,
          suspendedHospitals: res.data.filter(h => !h.isActive).length
        });
      } catch (error) {
        console.error("Failed to fetch super admin data", error);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const toggleHospitalStatus = async (id, currentStatus) => {
    try {
      await api.patch(`/admin/hospitals/${id}/status`, { isActive: !currentStatus });
      // Refresh list
      const res = await api.get('/admin/hospitals');
      setHospitals(res.data);
      setStats({
        totalHospitals: res.data.length,
        activeHospitals: res.data.filter(h => h.isActive).length,
        suspendedHospitals: res.data.filter(h => !h.isActive).length
      });
    } catch (error) {
      alert("Failed to update hospital status");
    }
  };

  if (loading) return <div className="flex justify-center py-20"><TrendingUp className="animate-spin h-8 w-8 text-primary" /></div>;

  return (
    <div className="space-y-6">
      <button onClick={() => window.location.href = '/dashboard'} className="flex items-center gap-2 text-gray-500 hover:text-primary transition text-sm">
        <ArrowLeft className="h-4 w-4" /> Back to Main Dashboard
      </button>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-200">
          <div className="flex items-center gap-3 mb-2">
            <Building2 className="h-8 w-8 text-blue-600" />
            <span className="text-sm font-medium text-gray-500">Total Hospitals</span>
          </div>
          <p className="text-3xl font-bold text-gray-900">{stats.totalHospitals}</p>
        </div>
        <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-200">
          <div className="flex items-center gap-3 mb-2">
            <Users className="h-8 w-8 text-green-600" />
            <span className="text-sm font-medium text-gray-500">Active</span>
          </div>
          <p className="text-3xl font-bold text-green-600">{stats.activeHospitals}</p>
        </div>
        <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-200">
          <div className="flex items-center gap-3 mb-2">
            <AlertCircle className="h-8 w-8 text-red-600" />
            <span className="text-sm font-medium text-gray-500">Suspended</span>
          </div>
          <p className="text-3xl font-bold text-red-600">{stats.suspendedHospitals}</p>
        </div>
      </div>

      {/* Hospitals Table */}
      <div className="bg-white rounded-2xl overflow-hidden">
        <table className="w-full text-left">
          <thead className="bg-gray-50/50">
            <tr>
              <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase">Hospital Name</th>
              <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase">Code</th>
              <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase">Email</th>
              <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase">Status</th>
              <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {hospitals.map(hospital => (
              <tr key={hospital.id} className="hover:bg-gray-50/50">
                <td className="px-6 py-4 font-medium text-gray-900">{hospital.name}</td>
                <td className="px-6 py-4 text-gray-600">{hospital.hospitalCode}</td>
                <td className="px-6 py-4 text-gray-600">{hospital.email}</td>
                <td className="px-6 py-4">
                  <span className={`px-3 py-1 text-xs font-semibold rounded-full ${
                    hospital.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                  }`}>
                    {hospital.isActive ? 'Active' : 'Suspended'}
                  </span>
                </td>
                <td className="px-6 py-4">
                  <button 
                    onClick={() => toggleHospitalStatus(hospital.id, hospital.isActive)}
                    className={`px-3 py-1 text-xs font-medium rounded-lg transition ${
                      hospital.isActive 
                        ? 'bg-red-50 text-red-600 hover:bg-red-100' 
                        : 'bg-green-50 text-green-600 hover:bg-green-100'
                    }`}
                  >
                    {hospital.isActive ? 'Suspend' : 'Activate'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}