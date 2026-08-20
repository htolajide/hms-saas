import { useState } from 'react';
import api from '../services/api';
import { X, Loader2 } from 'lucide-react';

export default function AddTriageModal({ patientId, onClose, onSaved }) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [formData, setFormData] = useState({
    patientId: patientId,
    hospitalId: 1,
    temperature: '',
    bloodPressureSystolic: '',
    bloodPressureDiastolic: '',
    pulseRate: '',
    respiratoryRate: '',
    weight: '',
    height: '',
    chiefComplaint: '',
    triageCategory: 'NON_URGENT',
    notes: ''
  });

  const handleChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true); setError(''); setSuccess('');
    try {
      await api.post('/triage', formData);
      setSuccess('Triage recorded successfully!');
      onSaved();
      setTimeout(onClose, 1500);
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to record triage');
    } finally { setLoading(false); }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-3xl max-h-[90vh] overflow-y-auto">
        <div className="flex justify-between items-center p-6 border-b border-gray-100 sticky top-0 bg-white z-10">
          <h3 className="text-xl font-bold text-gray-800">Record Patient Vitals</h3>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600"><X className="h-6 w-6" /></button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-6">
          {error && <div className="bg-red-50 text-red-600 p-3 rounded-lg text-sm">{error}</div>}
          {success && <div className="bg-green-50 text-green-700 p-3 rounded-lg text-sm font-medium">{success}</div>}

          {/* Vitals Section */}
          <div>
            <h4 className="text-sm font-semibold text-gray-500 uppercase mb-3">Vital Signs</h4>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Temperature (°C)</label>
                <input name="temperature" type="number" step="0.1" value={formData.temperature} onChange={handleChange} className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 outline-none" placeholder="37.0" />
              </div>
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">BP Systolic</label>
                  <input name="bloodPressureSystolic" type="number" value={formData.bloodPressureSystolic} onChange={handleChange} className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 outline-none" placeholder="120" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">BP Diastolic</label>
                  <input name="bloodPressureDiastolic" type="number" value={formData.bloodPressureDiastolic} onChange={handleChange} className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 outline-none" placeholder="80" />
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Pulse Rate (bpm)</label>
                <input name="pulseRate" type="number" value={formData.pulseRate} onChange={handleChange} className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 outline-none" placeholder="72" />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Respiratory Rate</label>
                <input name="respiratoryRate" type="number" value={formData.respiratoryRate} onChange={handleChange} className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 outline-none" placeholder="16" />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Weight (kg)</label>
                <input name="weight" type="number" step="0.1" value={formData.weight} onChange={handleChange} className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 outline-none" placeholder="70.5" />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Height (cm)</label>
                <input name="height" type="number" step="0.1" value={formData.height} onChange={handleChange} className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 outline-none" placeholder="175" />
              </div>
            </div>
          </div>

          {/* Clinical Info */}
          <div className="border-t pt-4">
            <h4 className="text-sm font-semibold text-gray-500 uppercase mb-3">Clinical Information</h4>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Chief Complaint</label>
                <textarea name="chiefComplaint" value={formData.chiefComplaint} onChange={handleChange} rows="3" className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 outline-none" placeholder="Describe the patient's main complaint..." />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Triage Category</label>
                <select name="triageCategory" value={formData.triageCategory} onChange={handleChange} className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 outline-none">
                  <option value="NON_URGENT">Non-Urgent (Green)</option>
                  <option value="URGENT">Urgent (Yellow)</option>
                  <option value="EMERGENCY">Emergency (Red)</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Nurse Notes</label>
                <textarea name="notes" value={formData.notes} onChange={handleChange} rows="2" className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 outline-none" placeholder="Additional observations..." />
              </div>
            </div>
          </div>

          <div className="flex justify-end gap-3 pt-4 border-t border-gray-100">
            <button type="button" onClick={onClose} className="px-4 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200">Cancel</button>
            <button type="submit" disabled={loading} className="px-6 py-2 bg-orange-600 text-white rounded-lg hover:bg-orange-700 flex items-center gap-2 disabled:opacity-50">
              {loading ? <Loader2 className="h-4 w-4 animate-spin" /> : 'Record Vitals'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}